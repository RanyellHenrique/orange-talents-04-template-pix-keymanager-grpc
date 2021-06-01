package br.com.zup.ranyell.keymanager.compartilhado.validacao

import br.com.zup.ranyell.keymanager.pix.registra.NovaChavePix
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Singleton
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.TYPE
import kotlin.reflect.KClass

@MustBeDocumented
@Target(CLASS, TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = [ValidPixKeyValidator::class])
annotation class ValidPixKey (
    val message: String = "Chave pix inválida para o tipo \${validatedValue.tipo}",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = []
    )

@Singleton
class ValidPixKeyValidator: ConstraintValidator<ValidPixKey, NovaChavePix> {

    override fun isValid(
        value: NovaChavePix?,
        annotationMetadata: AnnotationValue<ValidPixKey>,
        context: ConstraintValidatorContext
    ): Boolean {
        if(value!!.tipoDeChave == null) {
            return  true
        }
        return value!!.tipoDeChave!!.valida(value.chave)
    }

}
