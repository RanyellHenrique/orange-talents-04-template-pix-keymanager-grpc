package br.com.zup.ranyell.keymanager.compartilhado

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.nio.file.Files
import java.nio.file.Paths

class Instituicoes {
    companion object {
        fun buscaNomePorIspb(ispb: String): String {
            val reader = Files.newBufferedReader(Paths.get("./ParticipantesSTRport.csv"))
            val cvParser = CSVParser(reader, CSVFormat.DEFAULT.withTrim())

            return cvParser
                .filter { it.get(0).equals(ispb) }
                .map { it.get(1)}
                .ifEmpty { listOf("Nome da instituição não encontrada") }
                .first()

        }
    }

}