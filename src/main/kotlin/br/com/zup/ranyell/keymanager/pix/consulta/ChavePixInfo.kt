package br.com.zup.ranyell.keymanager.pix.consulta

import br.com.zup.ranyell.keymanager.ConsultaChavePixResponse.*
import br.com.zup.ranyell.keymanager.TipoDeChave
import br.com.zup.ranyell.keymanager.TipoDeConta
import br.com.zup.ranyell.keymanager.compartilhado.Instituicoes
import br.com.zup.ranyell.keymanager.pix.ChavePix
import java.time.LocalDateTime

data class ChavePixInfo(
    val pixId: String? = null,
    val clienteId: String? = null,
    val chave: String,
    val titularNome: String,
    val titularCpf: String,
    val tipo: TipoDeChave,
    val conta: ContaAssociada,
    val registradaEm: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun of(chavePix: ChavePix): ChavePixInfo {
            return ChavePixInfo(
                pixId = chavePix.id,
                clienteId = chavePix.conta.titular.id,
                chave = chavePix.chave,
                titularNome = chavePix.conta.titular.nome,
                titularCpf = chavePix.conta.titular.cpf,
                tipo = TipoDeChave.valueOf(chavePix.tipoDeChave.name),
                registradaEm = chavePix.registradaEm,
                conta = ContaAssociada(
                    nome = Instituicoes.buscaNomePorIspb(chavePix.conta.instituicao.ispb),
                    agencia = chavePix.conta.agencia,
                    numero = chavePix.conta.numero,
                    tipoDeConta = TipoDeConta.valueOf(chavePix.conta.tipo.name)
                )
            )
        }
    }
}