package br.com.zup.ranyell.keymanager.pix

import br.com.zup.ranyell.keymanager.conta.Conta
import org.hibernate.annotations.GenericGenerator
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
class ChavePix(
    @Column(unique = true)
    @NotBlank
    val chave: String,
    @Enumerated(EnumType.STRING)
    @NotNull
    val tipoDeChave: TipoDeChave,
    @Embedded
    val conta: Conta,
    val registradaEm: LocalDateTime = LocalDateTime.now()
){

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    var id: String? = null

}