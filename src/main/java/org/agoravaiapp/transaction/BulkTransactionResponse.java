package org.agoravaiapp.transaction;

import java.util.List;

/**
 * Resultado de um lote.
 *
 * <p>{@code results} traz os lancamentos efetivamente criados e {@code errors} as
 * linhas recusadas, cada uma com o indice que ocupava no pedido -- e o indice que
 * permite a UI destacar exatamente a linha problematica da planilha, em vez de
 * falhar o lote inteiro sem dizer onde.</p>
 */
public record BulkTransactionResponse(
        List<TransactionDto> results,
        List<BulkError> errors) {

    /** @param index posicao no array {@code transactions} enviado (base 0). */
    public record BulkError(int index, String message) {
    }
}
