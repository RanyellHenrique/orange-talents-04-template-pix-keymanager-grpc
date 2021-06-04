package br.com.zup.ranyell.keymanager.compartilhado

import br.com.zup.ranyell.keymanager.compartilhado.excecao.RecursoNaoEncontradoException
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.nio.file.Files
import java.nio.file.Paths

class Instituicoes {
    companion object {

        val reader = Files.newBufferedReader(Paths.get("./ParticipantesSTRport.csv"))
        val cvParser = CSVParser(reader, CSVFormat.DEFAULT)

        fun buscaNomePorIspb(ispb: String): String {
            return cvParser
                .filter { it.get(0).equals(ispb) }
                .map { it.get(5).trim() }
                .ifEmpty { throw RecursoNaoEncontradoException("ISPB não encontrado") }
                .first()
        }
    }

}