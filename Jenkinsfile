pipeline {
    agent {
        kubernetes {
            yaml '''
apiVersion: v1
kind: Pod
metadata:
  labels:
    app: jenkins-agent
spec:
  serviceAccountName: my-jenkins
  containers:
  - name: jnlp
    image: jenkins/inbound-agent:alpine
  - name: kaniko
    image: gcr.io/kaniko-project/executor:debug
    command:
    - /busybox/cat
    tty: true
    volumeMounts:
    - name: docker-config
      mountPath: /kaniko/.docker
  # Container especialista em Kubernetes para o Deploy
  - name: kubectl
    image: dtzar/helm-kubectl:latest
    command:
    - cat
    tty: true
  volumes:
  - name: docker-config
    secret:
      secretName: docker-hub-auth-secret
'''
        }
    }

    options {
        disableConcurrentBuilds()
    }

    environment {
        DOCKER_IMAGE = 'gaabreiser/core-service'
        APP_NAME     = 'core-service'
        APP_PORT     = '8082'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Test & Compile') {
            steps {
                container('jnlp') {
                    script {
                        if (isUnix()) {
                            sh 'chmod +x mvnw'
                            sh './mvnw -B test -DskipTests'
                        } else {
                            bat 'mvnw.cmd -B test -DskipTests'
                        }
                        env.IMAGE_TAG = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                    }
                }
            }
        }

        stage('Build & Push') {
            steps {
                container('kaniko') {
                    script {
                        def destinations = "--destination ${DOCKER_IMAGE}:${env.IMAGE_TAG}"

                        if (env.BRANCH_NAME == 'main') {
                            destinations += " --destination ${DOCKER_IMAGE}:latest"
                        }

                        sh """
                            /kaniko/executor \
                            --context `pwd` \
                            --dockerfile `pwd`/Dockerfile \
                            ${destinations}
                        """
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                container('kubectl') {
                    script {
                        switch (env.BRANCH_NAME) {
                            case 'main':
                                env.NAMESPACE = 'prod'
                                env.DOMAIN    = 'core-prod.agoravaiapp.com'
                                break
                            case 'homol':
                                env.NAMESPACE = 'hom'
                                env.DOMAIN    = 'core-hom.agoravaiapp.com'
                                break
                            case 'dev':
                                env.NAMESPACE = 'dev'
                                env.DOMAIN    = 'core-dev.agoravaiapp.com'
                                break
                            default:
                                error "Branch '${env.BRANCH_NAME}' não está configurada para deploy."
                        }

                        withEnv([
                            "NAMESPACE=${env.NAMESPACE}",
                            "DOMAIN=${env.DOMAIN}",
                            "IMAGE=${DOCKER_IMAGE}:${env.IMAGE_TAG}",
                            "APP_PORT=${env.APP_PORT}"
                        ]) {
                            sh '''
                                set -e

                                kubectl create namespace "$NAMESPACE" --dry-run=client -o yaml | kubectl apply -f -

                                for resource in deployment service ingress; do
                                    sed -e "s|\\${NAMESPACE}|$NAMESPACE|g" \
                                        -e "s|\\${DOMAIN}|$DOMAIN|g" \
                                        -e "s|\\${IMAGE}|$IMAGE|g" \
                                        -e "s|\\${APP_PORT}|$APP_PORT|g" \
                                        "k8s/${resource}.yaml" | kubectl apply -f -
                                done

                                kubectl -n "$NAMESPACE" rollout status deployment/core-service --timeout=180s
                            '''
                        }
                    }
                }
            }
        }
    }
}
