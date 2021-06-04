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
    constructor(chavePix: ChavePix) : this(
        keyType = chavePix.tipoDeChave!!.type,
        key = chavePix.chave,
        bankAccount = BankAccount(chavePix.conta),
        owner = Owner(chavePix.conta)
    )
}

data class CreatePixKeyResponse(
    val keyType: KeyType,
    val key: String?,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime
) {
}

enum class KeyType(val tipoDeChave: TipoDeChave) {
    CPF(TipoDeChave.CPF),
    RANDOM(TipoDeChave.CHAVE_ALEATORIA),
    EMAIL(TipoDeChave.EMAIL),
    CNPJ(TipoDeChave.CPF),
    PHONE(TipoDeChave.TELEFONE);
}

enum class AccountType(
    val tipoDeConta: TipoDeConta
) {
    CACC(TipoDeConta.CONTA_CORRENTE),
    SVGS(TipoDeConta.CONTA_POUPANCA);
}

data class BankAccount(
    val participant: String,
    val branch: String,
    val accountNumber: String,
    val accountType: AccountType,
) {
    constructor(conta: Conta) : this(
        participant = conta.instituicao.ispb,
        branch = conta.agencia,
        accountNumber = conta.numero,
        accountType = conta.tipo.type
    )
}

data class Owner(
    val type: String = "NATURAL_PERSON",
    val name: String,
    val taxIdNumber: String
) {
    constructor(conta: Conta) : this(
        name = conta.titular.nome,
        taxIdNumber = conta.titular.cpf
    )
}