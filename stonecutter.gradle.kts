plugins {
	id("dev.kikugie.stonecutter")
}

stonecutter active "1.21.5"

stonecutter parameters {
	// Cross-version constants Java-код может проверить: //? if >=1.21.4
	// Ничего явно регистрировать не нужно — версии сравниваются по literal.
}
