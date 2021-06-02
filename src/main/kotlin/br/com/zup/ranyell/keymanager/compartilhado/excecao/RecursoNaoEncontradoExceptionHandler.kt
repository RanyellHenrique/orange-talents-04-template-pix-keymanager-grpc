package br.com.zup.ranyell.keymanager.compartilhado.excecao

import br.com.zup.ranyell.keymanager.compartilhado.excecao.handler.ExceptionHandler
import br.com.zup.ranyell.keymanager.compartilhado.excecao.handler.ExceptionHandler.*
import io.grpc.Status
import javax.inject.Singleton

@Singleton
class RecursoNaoEncontradoExceptionHandler: ExceptionHandler<RecursoNaoEncontradoException> {
    override fun handle(e: RecursoNaoEncontradoException): StatusWithDetails {
        return StatusWithDetails(Status.NOT_FOUND
            .withDescription(e.message)
            .withCause(e))
    }

    override fun supports(e: Exception): Boolean {
        return e is RecursoNaoEncontradoException
    }
}

class RecursoNaoEncontradoException(mensagem: String): RuntimeException(mensagem)