package br.com.zup.ranyell.keymanager.conta

import br.com.zup.ranyell.keymanager.sistemabcb.AccountType

enum class TipoDeConta(val type: AccountType) {
    CONTA_CORRENTE (AccountType.CACC),
    CONTA_POUPANCA (AccountType.SVGS)
}