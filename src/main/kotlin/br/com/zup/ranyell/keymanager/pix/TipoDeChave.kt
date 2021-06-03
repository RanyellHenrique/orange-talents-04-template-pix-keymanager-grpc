package br.com.zup.ranyell.keymanager.pix

import br.com.zup.ranyell.keymanager.sistemabcb.KeyType
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator


enum class TipoDeChave(val type: KeyType) {
    CHAVE_ALEATORIA (KeyType.RANDOM){
        override fun valida(chave: String?): Boolean {
            return chave.isNullOrBlank()
        }
    },
    CPF (KeyType.CPF) {
        override fun valida(chave: String?): Boolean {
            if(chave.isNullOrBlank()) {
                return false
            }
            return CPFValidator().run {
                initialize(null)
                isValid(chave, null)
            }
        }
    },
    EMAIL (KeyType.EMAIL){
        override fun valida(chave: String?): Boolean {
            if(chave.isNullOrBlank()) {
                return false
            }
            return EmailValidator().run {
                initialize(null)
                isValid(chave,null)
            }
        }
    },
    TELEFONE (KeyType.PHONE){
        override fun valida(chave: String?): Boolean {
            if(chave.isNullOrBlank()) {
                return false
            }
            return chave.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
        }
    };

    abstract fun valida(chave: String?): Boolean
}