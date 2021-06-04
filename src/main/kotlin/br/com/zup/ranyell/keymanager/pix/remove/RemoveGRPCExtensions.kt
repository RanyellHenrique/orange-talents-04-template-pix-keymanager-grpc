package br.com.zup.ranyell.keymanager.pix.remove

import br.com.zup.ranyell.keymanager.RemoveChavePixRequest

fun RemoveChavePixRequest.toModel(): RemoveChavePix {
    return RemoveChavePix(
        idCliente = clienteId,
        idPix = pixId
    )
}