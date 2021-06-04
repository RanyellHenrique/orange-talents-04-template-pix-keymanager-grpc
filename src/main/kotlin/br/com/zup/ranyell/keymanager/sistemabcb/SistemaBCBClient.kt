package br.com.zup.ranyell.keymanager.sistemabcb

import br.com.zup.ranyell.keymanager.pix.ChavePix
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client
import java.time.LocalDateTime
import javax.validation.constraints.NotNull

@Client("\${bcb.pix.url}")
interface SistemaBCBClient {

    @Post(
        value = "/api/v1/pix/keys",
        consumes = [MediaType.APPLICATION_XML],
        produces = [MediaType.APPLICATION_XML]
    )
    fun registra(@Body createPixKeyRequest: CreatePixKeyRequest?): HttpResponse<CreatePixKeyResponse>

    @Delete(
        value = "/api/v1/pix/keys/{key}",
        consumes = [MediaType.APPLICATION_XML],
        produces = [MediaType.APPLICATION_XML]
    )
    fun deleta(@Body deleteRequest: DeletePixKeyRequest?, @PathVariable key: String?): HttpResponse<DeletePixKeyResponse>

    @Get(value = "/api/v1/pix/keys/{key}")
    fun consulta(@PathVariable key: String): HttpResponse<PixKeyResponse?>


}