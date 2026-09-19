package org.agoravaiapp.transaction;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * Lote de lancamentos, usado pelo "modo planilha" e pela confirmacao de extrato.
 *
 * <p>O teto de {@value #MAX_ITEMS} itens existe para uma requisicao nao virar um
 * lote ilimitado: um extrato anual de cartao passa de mil linhas, e importar tudo
 * numa transacao unica prenderia conexao e memoria. O cliente pagina.</p>
 */
public record BulkTransactionRequest(
        @NotEmpty(message = "envie ao menos um lancamento em 'transactions'")
        @Size(max = MAX_ITEMS, message = "no maximo " + MAX_ITEMS + " lancamentos por requisicao")
        @Valid List<CreateTransactionRequest> transactions) {

    public static final int MAX_ITEMS = 500;
}
