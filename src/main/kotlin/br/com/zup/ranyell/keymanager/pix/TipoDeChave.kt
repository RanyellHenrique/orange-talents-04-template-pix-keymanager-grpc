package br.com.zup.ranyell.keymanager.pix



enum class TipoDeChave(val validacao: String) {
    CHAVE_ALEATORIA (validacao = "") ,
    CPF (validacao ="^[0-9]{11}\$"),
    TELEFONE (validacao ="^\\+[1-9][0-9]\\d{1,14}\$"),
    EMAIL (validacao ="^(\\w|\\.)+@(\\w)+\\.(\\w){3}(\\.(\\w){2})?")
}