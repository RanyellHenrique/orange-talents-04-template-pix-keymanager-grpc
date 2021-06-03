package br.com.zup.ranyell.keymanager.sistemabcb

import br.com.zup.ranyell.keymanager.conta.Conta
import br.com.zup.ranyell.keymanager.conta.TipoDeConta
import br.com.zup.ranyell.keymanager.pix.ChavePix
import br.com.zup.ranyell.keymanager.pix.TipoDeChave
import br.com.zup.ranyell.keymanager.pix.registra.RegistraChavePix
import java.time.LocalDateTime

data class CreatePixKeyRequest(
    val keyType: KeyType,
    val key: String?,
    val bankAccount: BankAccount,
    val owner: Owner
) {
    constructor(conta: Conta, registraChavePix: RegistraChavePix) : this(
        keyType = registraChavePix.tipoDeChave!!.type,
        key = registraChavePix.chave,
        bankAccount = BankAccount(
            participant = conta.instituicao.ispb,
            branch = conta.agencia,
            accountNumber = conta.numero,
            accountType = conta.tipo.type
        ),
        owner = Owner(
            name = conta.titular.nome,
            taxIdNumber = conta.titular.cpf
        )
    )
}

data class CreatePixKeyResponse(
    val keyType: KeyType,
    val key: String?,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime
) {
    fun toModel(conta: Conta, tipoDeChave: TipoDeChave): ChavePix {
        return ChavePix(
            chave = key!!,
            conta = conta,
            registradaEm = createdAt,
            tipoDeChave = tipoDeChave
        )
    }
}

enum class KeyType {
    CPF, RANDOM, EMAIL, CNPJ, PHONE;
}

enum class AccountType {
    CACC, SVGS;
}

data class BankAccount(
    val participant: String,
    val branch: String,
    val accountNumber: String,
    val accountType: AccountType,
) {
}

data class Owner(
    val type: String = "NATURAL_PERSON",
    val name: String,
    val taxIdNumber: String
) {
}