package br.com.zup.ranyell.keymanager.compartilhado.excecao

import br.com.zup.ranyell.keymanager.compartilhado.excecao.handler.ExceptionHandler
import br.com.zup.ranyell.keymanager.compartilhado.excecao.handler.ExceptionHandler.StatusWithDetails
import io.grpc.Status
import javax.inject.Singleton

@Singleton
class RecursoExistenteExceptionHandler: ExceptionHandler<RecursoExistenteException> {
    override fun handle(e: RecursoExistenteException): StatusWithDetails {
        return StatusWithDetails(
            Status.ALREADY_EXISTS
                .withDescription(e.message)
                .withCause(e)
        )
    }

    override fun supports(e: Exception): Boolean {
        return e is RecursoExistenteException
    }

}
class RecursoExistenteException(mensagem: String) : RuntimeException(mensagem)

