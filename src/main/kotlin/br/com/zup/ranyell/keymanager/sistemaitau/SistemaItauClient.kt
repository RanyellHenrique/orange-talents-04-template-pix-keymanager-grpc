package br.com.zup.ranyell.keymanager.sistemaitau

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client("\${itau.contas.url}")
interface SistemaItauClient {

    @Get("/api/v1/clientes/{id}/contas")
    fun consulta(@PathVariable id: String, @QueryValue tipo: String): HttpResponse<ContaResponse>
}