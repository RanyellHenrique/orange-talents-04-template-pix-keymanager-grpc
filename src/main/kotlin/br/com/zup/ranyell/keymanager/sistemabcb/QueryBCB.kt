package br.com.zup.ranyell.keymanager.sistemabcb

import br.com.zup.ranyell.keymanager.TipoDeChave
import br.com.zup.ranyell.keymanager.TipoDeConta
import br.com.zup.ranyell.keymanager.pix.consulta.ChavePixInfo
import br.com.zup.ranyell.keymanager.pix.consulta.ContaAssociada
import java.time.LocalDateTime

data class PixKeyResponse(
    val keyType: KeyType,
    val key: String?,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime
) {

    fun toModel(): ChavePixInfo {
        return ChavePixInfo(
            pixId = null,
            clienteId = null,
            chave = key!!,
            tipo = when (keyType) {
                KeyType.CPF -> TipoDeChave.CPF
                KeyType.EMAIL -> TipoDeChave.EMAIL
                KeyType.PHONE -> TipoDeChave.TELEFONE
                KeyType.RANDOM -> TipoDeChave.CHAVE_ALEATORIA
                else -> TipoDeChave.UNKNOWN_TIPO_CHAVE
            },
            registradaEm = createdAt,
            titularNome = owner.name,
            titularCpf = owner.taxIdNumber,
            conta = ContaAssociada(
                nome = bankAccount.participant,
                agencia = bankAccount.branch,
                numero = bankAccount.accountNumber,
                tipoDeConta = when(bankAccount.accountType) {
                    AccountType.CACC -> TipoDeConta.CONTA_CORRENTE
                    AccountType.SVGS -> TipoDeConta.CONTA_POUPANCA
                }
            )
        )
    }
}