package org.agoravaiapp.dashboard;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Cache do dashboard via Redis — <strong>semi-implementado</strong>.
 *
 * <p>Fica desabilitado por padrao ({@code agoravai.redis.enabled=false}) e nao
 * injeta o client Redis, entao o servico inicia sem depender de um Redis ativo.
 * Para ativar, injete {@code io.quarkus.redis.datasource.RedisDataSource} e
 * implemente leitura/escrita da chave {@code dashboard:{userId}}.</p>
 */
@ApplicationScoped
public class DashboardCache {

    @ConfigProperty(name = "agoravai.redis.enabled", defaultValue = "false")
    boolean enabled;

    public Optional<DashboardResponse> get(String userId) {
        if (!enabled) {
            return Optional.empty();
        }
        // TODO(Fase 3): ler dashboard:{userId} do Redis com TTL.
        return Optional.empty();
    }

    public void put(String userId, DashboardResponse response) {
        if (!enabled) {
            return;
        }
        // TODO(Fase 3): gravar dashboard:{userId} no Redis com TTL curto.
    }
}
