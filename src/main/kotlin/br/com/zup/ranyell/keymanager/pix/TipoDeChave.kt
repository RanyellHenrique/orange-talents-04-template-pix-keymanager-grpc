package br.com.zup.ranyell.keymanager.pix

import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator


enum class TipoDeChave {
    CHAVE_ALEATORIA {
        override fun valida(chave: String?): Boolean {
            return chave.isNullOrBlank()
        }
    },
    CPF {
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
    EMAIL {
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
    TELEFONE {
        override fun valida(chave: String?): Boolean {
            if(chave.isNullOrBlank()) {
                return false
            }
            return chave.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
        }
    };

    abstract fun valida(chave: String?): Boolean
}