package br.com.zup.ranyell.keymanager.compartilhado.excecao

import br.com.zup.ranyell.keymanager.compartilhado.excecao.handler.ExceptionHandler
import br.com.zup.ranyell.keymanager.compartilhado.excecao.handler.ExceptionHandler.StatusWithDetails
import io.grpc.Status
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
class ArgumentosInvalidosExceptionHandler : ExceptionHandler<ConstraintViolationException> {

    override fun handle(e: ConstraintViolationException): StatusWithDetails {
        println("${e.message}, ${e.constraintViolations}")
        return StatusWithDetails(Status.INVALID_ARGUMENT
            .withDescription(e.message)
            .withCause(e))
    }

    override fun supports(e: Exception): Boolean {
        return e is ConstraintViolationException
    }
}