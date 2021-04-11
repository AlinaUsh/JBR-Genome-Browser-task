import java.nio.file.Path

fun main() {
    var bedReader = BedReaderImpl()
    bedReader.createIndex(
        Path.of("test/resources/BedFile3.txt"), Path.of("test/resources/BedFile3Index.txt")
    )
}