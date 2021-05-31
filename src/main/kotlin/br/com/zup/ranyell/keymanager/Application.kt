package br.com.zup.ranyell.keymanager

import io.micronaut.runtime.Micronaut.*
fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("br.com.zup.ranyell.keymanager")
		.start()
}

