package br.com.zup.ranyell.keymanager.pix

import br.com.zup.ranyell.keymanager.conta.Conta
import org.hibernate.annotations.GenericGenerator
import javax.persistence.*

@Entity
class Pix(
    @Column(unique = true)
    val chave: String,
    @Enumerated(EnumType.STRING)
    val tipoDeChave: TipoDeChave,
    val conta: Conta
){

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    var id: String? = null

}