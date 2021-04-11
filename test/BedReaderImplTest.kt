import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.nio.file.Path
import java.util.*
import kotlin.test.assertEquals

class BedReaderImplTest {

    @Test
    fun `Test createIndex`() {
        val bedReader = BedReaderImpl()
        val indexPath = Path.of("test/resources/BedFile2Indexfile.txt")
        val bedPath = Path.of("test/resources/BedFile2.txt")

        bedReader.createIndex(bedPath, indexPath)

        val hashChr11 = Objects.hashCode("chr11")
        val expectedOutput = listOf<String>(
            "${hashChr11}\tchr11\t5246919\t5246920\t0",
            "${hashChr11}\tchr11\t5247945\t5247946\t2",
            "${hashChr11}\tchr11\t5248234\t5248235\t4",
            "${hashChr11}\tchr11\t5255415\t5255416\t3",
            "${hashChr11}\tchr11\t5255660\t5255661\t1"
        )

        var i = 0
        try {
            for (line in indexPath.toFile().readLines()) {
                assertEquals(line, expectedOutput.elementAt(i))
                i++;
            }
        } catch (e: IndexOutOfBoundsException) {
            assert(false)
        }
    }

    @Test
    fun `Test loadIndex`() {
        val bedReaderImpl = BedReaderImpl()
        val indexPath = Path.of("test/resources/BedFile2Indexfile.txt")
        val bedPath = Path.of("test/resources/BedFile2.txt")

        bedReaderImpl.createIndex(bedPath, indexPath)

        val bedReaderImpl2 = BedReaderImpl()
        val bedIndex = bedReaderImpl2.loadIndex(indexPath)

        val hashChr11 = Objects.hashCode("chr11")
        var expectedHshTable: HashMap<Int, MutableList<BedIndexEntry>> = HashMap()
        expectedHshTable[hashChr11] = mutableListOf(
            BedIndexEntry("chr11", 5246919, 5246920, 0),
            BedIndexEntry("chr11", 5247945, 5247946, 2),
            BedIndexEntry("chr11", 5248234, 5248235, 4),
            BedIndexEntry("chr11", 5255415, 5255416, 3),
            BedIndexEntry("chr11", 5255660, 5255661, 1)
        )

        assertEquals(expectedHshTable, bedIndex.getHashTable())
    }

    @Test
    fun `Test findWithIndex`() {
        val bedReader = BedReaderImpl()

        val indexPath = Path.of("test/resources/BedFile3Indexfile.txt")
        val bedPath = Path.of("test/resources/BedFile3.txt")

        bedReader.createIndex(bedPath, indexPath)
        val bedIndex = bedReader.loadIndex(indexPath)

        assertEquals(
            listOf(
                BedEntry("chr7", 127471196, 127472380, listOf("Pos1", "0", "+")),
                BedEntry("chr7", 127472372, 127474697, listOf("Pos3", "0", "+"))
            ),
            bedReader.findWithIndex(bedIndex, bedPath, "chr7", 127471195, 127474699)
        )

        assertEquals(
            listOf(
                BedEntry("chr9", 127478198, 127479365, listOf("Neg3", "0", "-"))
            ),
            bedReader.findWithIndex(bedIndex, bedPath, "chr9", 0, 927474699)
        )

        assertEquals(
            listOf(
                BedEntry("chr7", 127471196, 127472380, listOf("Pos1", "0", "+")),
                BedEntry("chr7", 127472372, 127474697, listOf("Pos3", "0", "+")),
                BedEntry("chr7", 127474697, 127475864, listOf("Pos4", "0", "+")),
                BedEntry("chr7", 127477031, 127478198, listOf("Neg2", "0", "-")),
                BedEntry("chr7", 127479365, 127480532, listOf("Pos5", "0", "+")),
                BedEntry("chr7", 127480532, 127481699, listOf("Neg4", "0", "-"))
            ),
            bedReader.findWithIndex(bedIndex, bedPath, "chr7", 127471190, 127481700)
        )

        Assertions.assertThrows(IllegalArgumentException::class.java) {
            bedReader.findWithIndex(bedIndex, bedPath, "chr10", 127471190, 127481700)
        }

        assertEquals(
            listOf(),
            bedReader.findWithIndex(bedIndex, bedPath, "chr8", 127472000, 127472363)
        )
    }
}