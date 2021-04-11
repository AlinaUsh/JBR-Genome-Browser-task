import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.nio.file.Path
import java.util.*
import kotlin.collections.HashMap

class BedIndexImpTest {

    @Test
    fun `Test parseStringToBedEntry`() {
        val bedIndex = BedIndexImpl()
        assertEquals(
            bedIndex.parseStringToBedEntry("chr7\t127471196\t127472363\tPos1\t0\t+\t127471196\t127472363\t255,0,0"),
            BedEntry(
                "chr7", 127471196, 127472363,
                listOf("Pos1", "0", "+", "127471196", "127472363", "255,0,0")
            )
        )
    }

    @Test
    fun `Test parseStringToBedEntry without optional BED fields`() {
        val bedIndex = BedIndexImpl()
        assertEquals(
            bedIndex.parseStringToBedEntry("chr7\t127471196\t127472363"),
            BedEntry("chr7", 127471196, 127472363, listOf())
        )
    }

    @Test
    fun `Test parseStringToBedEntry not enough BED fields`() {
        val bedIndex = BedIndexImpl()
        assertThrows(IllegalArgumentException::class.java) {
            bedIndex.parseStringToBedEntry("chr7\t127471196")
        }
    }

    @Test
    fun `Test parseStringToBedEntry too many BED fields`() {
        val bedIndex = BedIndexImpl()
        assertThrows(IllegalArgumentException::class.java) {
            bedIndex.parseStringToBedEntry("chr7\t1\t2\t3\t4\t5\t6\t7\t8\t9\t10\t11\t12\t13")
        }
    }

    @Test
    fun `Test readFromBedFile throws if path is incorrect`() {
        val bedIndex = BedIndexImpl()
        assertThrows(IllegalArgumentException::class.java) {
            bedIndex.readFromBedFile(Path.of("test/incorrectPath/BEDfile.txt"))
        }
    }

    @Test
    fun `Test readFromBedFile throws if block is incorrect`() {
        val bedIndex = BedIndexImpl()
        assertThrows(IllegalArgumentException::class.java) {
            bedIndex.readFromBedFile(Path.of("test/resources/BEDfileIncorrectChromEnds.txt"))
        }
    }

    @Test
    fun `Test readFromIndexFile throws if path is incorrect`() {
        val bedIndex = BedIndexImpl()
        assertThrows(IllegalArgumentException::class.java) {
            bedIndex.readFromIndexFile(Path.of("test/incorrectPath/IndexFile.txt"))
        }
    }

    @Test
    fun `Test readFromIndexFile throws if position is incorrect`() {
        val bedIndex = BedIndexImpl()
        assertThrows(IllegalArgumentException::class.java) {
            bedIndex.readFromIndexFile(Path.of("test/resources/IndexFileIncorrectPosition.txt"))
        }
    }

    @Test
    fun `Test readFromIndexFile throws if block is incorrect`() {
        val bedIndex = BedIndexImpl()
        assertThrows(IllegalArgumentException::class.java) {
            bedIndex.readFromIndexFile(Path.of("test/resources/IndexFileIncorrectBlock.txt"))
        }
    }

    @Test
    fun `Test same BedIndex from BED file bedFileTest and BED index bedFileTestOut`() {
        var bedIndex1 = BedIndexImpl()
        var bedIndex2 = BedIndexImpl()

        bedIndex1.readFromBedFile(Path.of("test/resources/bedFileTest.txt"))
        bedIndex2.readFromIndexFile(Path.of("test/resources/bedFileTestOut.txt"))

        assertEquals(bedIndex1.getHashTable(), bedIndex2.getHashTable())
    }

    @Test
    fun `Test same BedIndex from BED file test1 and BED index test1Out`() {
        var bedIndex1 = BedIndexImpl()
        var bedIndex2 = BedIndexImpl()

        bedIndex1.readFromBedFile(Path.of("test/resources/test1.txt"))
        bedIndex2.readFromIndexFile(Path.of("test/resources/test1Out.txt"))

        assertEquals(bedIndex1.getHashTable(), bedIndex2.getHashTable())
    }

    @Test
    fun `Test readFromBedFile BedIndex emptyTest`() {
        val bedIndex = BedIndexImpl()
        bedIndex.readFromBedFile(Path.of("test/resources/emptyTest.txt"))

        val bedIndexExpected: HashMap<Int, MutableList<BedIndexEntry>> = HashMap()

        assertEquals(bedIndex.getHashTable(), bedIndexExpected)
    }

    @Test
    fun `Test readFromIndexFile BedIndex emptyTest`() {
        val bedIndex = BedIndexImpl()
        bedIndex.readFromIndexFile(Path.of("test/resources/emptyTest.txt"))

        val bedIndexExpected: HashMap<Int, MutableList<BedIndexEntry>> = HashMap()

        assertEquals(bedIndex.getHashTable(), bedIndexExpected)
    }

    @Test
    fun `Test readFromBedFile BedIndex test1`() {
        val bedIndex = BedIndexImpl()
        bedIndex.readFromBedFile(Path.of("test/resources/test1.txt"))

        val bedIndexExpected: HashMap<Int, MutableList<BedIndexEntry>> = HashMap()
        bedIndexExpected[Objects.hashCode("c1")] = mutableListOf(
            BedIndexEntry("c1", 1, 3, 0),
            BedIndexEntry("c1", 1, 3, 3)
        )
        bedIndexExpected[Objects.hashCode("c2")] = mutableListOf(
            BedIndexEntry("c2", 1, 10, 2)
        )
        bedIndexExpected[Objects.hashCode("c4")] = mutableListOf(
            BedIndexEntry("c4", 100, 1000, 1)
        )

        assertEquals(bedIndex.getHashTable(), bedIndexExpected)
    }

    @Test
    fun `Test readFromIndexFile BedIndex test1Out`() {
        val bedIndex = BedIndexImpl()
        bedIndex.readFromIndexFile(Path.of("test/resources/test1Out.txt"))

        val bedIndexExpected: HashMap<Int, MutableList<BedIndexEntry>> = HashMap()
        bedIndexExpected[Objects.hashCode("c1")] = mutableListOf(
            BedIndexEntry("c1", 1, 3, 0),
            BedIndexEntry("c1", 1, 3, 3)
        )
        bedIndexExpected[Objects.hashCode("c2")] = mutableListOf(
            BedIndexEntry("c2", 1, 10, 2)
        )
        bedIndexExpected[Objects.hashCode("c4")] = mutableListOf(
            BedIndexEntry("c4", 100, 1000, 1)
        )

        assertEquals(bedIndex.getHashTable(), bedIndexExpected)
    }


    @Test
    fun `Test formatBedIndexEntryForOutput`() {
        val bedIndex = BedIndexImpl()
        val entry = BedIndexEntry("chr7", 1, 100, 20)
        assertEquals("chr7\t1\t100\t20", bedIndex.formatBedIndexEntryForOutput(entry))
    }

    @Test
    fun `Test getBedIndexEntryHash by BedIndexEntry`() {
        val bedIndex = BedIndexImpl()
        val entry = BedIndexEntry("chr7", 1, 100, 20)
        val sameEntry = BedIndexEntry("chr7", 1, 100, 20)
        val differentEntry = BedIndexEntry("chr8", 1, 100, 20)
        val sameChromosomeDifferentBlock = BedIndexEntry("chr7", 10, 100, 20)

        assertEquals(
            bedIndex.getBedIndexEntryHash(entry),
            bedIndex.getBedIndexEntryHash(sameEntry)
        )
        assertEquals(
            bedIndex.getBedIndexEntryHash(sameEntry),
            bedIndex.getBedIndexEntryHash(sameEntry)
        )
        assertEquals(
            bedIndex.getBedIndexEntryHash(entry),
            bedIndex.getBedIndexEntryHash(sameEntry)
        )
        assertNotEquals(
            bedIndex.getBedIndexEntryHash(entry),
            bedIndex.getBedIndexEntryHash(differentEntry)
        )
        assertEquals(
            bedIndex.getBedIndexEntryHash(entry),
            bedIndex.getBedIndexEntryHash(sameChromosomeDifferentBlock)
        )
        assertNotEquals(
            bedIndex.getBedIndexEntryHash(sameEntry),
            bedIndex.getBedIndexEntryHash(differentEntry)
        )
    }

    @Test
    fun `Test getBedIndexEntryHash by String`() {
        val bedIndex = BedIndexImpl()
        val entry = BedIndexEntry("chr7", 1, 100, 20)
        val sameEntry = BedIndexEntry("chr7", 1, 100, 20)
        val differentEntry = BedIndexEntry("chr8", 1, 100, 20)
        val sameChromosomeDifferentBlock = BedIndexEntry("chr7", 10, 100, 20)

        assertEquals(
            bedIndex.getBedIndexEntryHash(entry.chromosome),
            bedIndex.getBedIndexEntryHash(sameEntry.chromosome)
        )
        assertEquals(
            bedIndex.getBedIndexEntryHash(sameEntry.chromosome),
            bedIndex.getBedIndexEntryHash(sameEntry.chromosome)
        )
        assertEquals(
            bedIndex.getBedIndexEntryHash(entry.chromosome),
            bedIndex.getBedIndexEntryHash(sameEntry.chromosome)
        )
        assertNotEquals(
            bedIndex.getBedIndexEntryHash(entry.chromosome),
            bedIndex.getBedIndexEntryHash(differentEntry.chromosome)
        )
        assertEquals(
            bedIndex.getBedIndexEntryHash(entry.chromosome),
            bedIndex.getBedIndexEntryHash(sameChromosomeDifferentBlock.chromosome)
        )
        assertNotEquals(
            bedIndex.getBedIndexEntryHash(sameEntry.chromosome),
            bedIndex.getBedIndexEntryHash(differentEntry.chromosome)
        )
    }

    @Test
    fun `Test addElement different chromosomes`() {
        val bedIndex = BedIndexImpl()

        val bedIndexExpected: HashMap<Int, MutableList<BedIndexEntry>> = HashMap()
        bedIndexExpected[Objects.hashCode("chr1")] = mutableListOf(
            BedIndexEntry("chr1", 1, 3, 0)
        )
        bedIndex.addElement(BedIndexEntry("chr1", 1, 3, 0))

        assertEquals(bedIndex.getHashTable(), bedIndexExpected)

        bedIndexExpected[Objects.hashCode("chr2")] = mutableListOf(
            BedIndexEntry("chr2", 1, 10, 2)
        )

        bedIndex.addElement(BedIndexEntry("chr2", 1, 10, 2))

        assertEquals(bedIndex.getHashTable(), bedIndexExpected)
    }

    @Test
    fun `Test addElement same chromosomes`() {
        val bedIndex = BedIndexImpl()

        val bedIndexExpected: HashMap<Int, MutableList<BedIndexEntry>> = HashMap()
        bedIndexExpected[Objects.hashCode("chr1")] = mutableListOf(
            BedIndexEntry("chr1", 1, 3, 0)
        )
        bedIndex.addElement(BedIndexEntry("chr1", 1, 3, 0))

        assertEquals(bedIndex.getHashTable(), bedIndexExpected)

        bedIndexExpected[Objects.hashCode("chr1")] = mutableListOf(
            BedIndexEntry("chr1", 1, 3, 0),
            BedIndexEntry("chr1", 1, 10, 2)
        )

        bedIndex.addElement(BedIndexEntry("chr1", 1, 10, 2))

        assertEquals(bedIndex.getHashTable(), bedIndexExpected)
    }

    @Test
    fun `Test same BedIndex from BED file BedFile2 and BED index BedFile2Index`() {
        val bedIndex1 = BedIndexImpl()
        val bedIndex2 = BedIndexImpl()

        bedIndex1.readFromBedFile(Path.of("test/resources/BedFile2.txt"))
        bedIndex2.readFromIndexFile(Path.of("test/resources/BedFile2Index.txt"))

        assertEquals(bedIndex1.getHashTable(), bedIndex2.getHashTable())

        val hashChr11 = Objects.hashCode("chr11")
        val bedIndexExpected: HashMap<Int, MutableList<BedIndexEntry>> = HashMap()
        bedIndexExpected[hashChr11] = mutableListOf(
            BedIndexEntry("chr11", 5246919, 5246920, 0),
            BedIndexEntry("chr11", 5247945, 5247946, 2),
            BedIndexEntry("chr11", 5248234, 5248235, 4),
            BedIndexEntry("chr11", 5255415, 5255416, 3),
            BedIndexEntry("chr11", 5255660, 5255661, 1)
        )

        assertEquals(bedIndexExpected, bedIndex1.getHashTable())
        assertEquals(bedIndexExpected, bedIndex2.getHashTable())
    }

    @Test
    fun `Test same BedIndex from BED file BedFileCrossingBlocks`() {
        val bedIndex = BedIndexImpl()

        bedIndex.readFromBedFile(Path.of("test/resources/BedFileCrossingBlocks.txt"))

        val hashChr22 = Objects.hashCode("chr22")
        val bedIndexExpected: HashMap<Int, MutableList<BedIndexEntry>> = HashMap()
        bedIndexExpected[hashChr22] = mutableListOf(
            BedIndexEntry("chr22", 1000, 5000, 0),
            BedIndexEntry("chr22", 2000, 6000, 1)
        )
        assertEquals(bedIndexExpected, bedIndex.getHashTable())
    }

    @Test
    fun `Test getChromosomeListPositions BedFileCrossingBlocks`() {
        val bedIndex = BedIndexImpl()
        bedIndex.readFromBedFile(Path.of("test/resources/BedFileCrossingBlocks.txt"))
        val hashChr22 = Objects.hashCode("chr22")
        assertEquals(
            listOf(0, 1),
            bedIndex.getChromosomeListPositions(hashChr22, 0, 10000)
        )
        assertEquals(
            listOf(1),
            bedIndex.getChromosomeListPositions(hashChr22, 2000, 10000)
        )
        assertEquals(
            listOf(0),
            bedIndex.getChromosomeListPositions(hashChr22, 1000, 6000)
        )
        assertEquals(
            listOf(0, 1),
            bedIndex.getChromosomeListPositions(hashChr22, 1000, 6001)
        )
        assertEquals(
            listOf(0),
            bedIndex.getChromosomeListPositions(hashChr22, 0, 5999)
        )
        assertTrue(
            bedIndex.getChromosomeListPositions(hashChr22, 0, 3000).isEmpty()
        )
    }

    @Test
    fun `Test getChromosomeListPositions BedFile1`() {
        val bedIndex = BedIndexImpl()
        bedIndex.readFromBedFile(Path.of("test/resources/BedFile1.txt"))

        val hashChr7 = Objects.hashCode("chr7")
        assertEquals(
            listOf(2, 3, 4, 5, 6),
            bedIndex.getChromosomeListPositions(hashChr7, 127473530, 127480532)
        )

        assertTrue(
            bedIndex.getChromosomeListPositions(hashChr7, 127473530, 127473531).isEmpty()
        )

        assertEquals(
            listOf(0, 1, 2, 3, 4, 5, 6, 7),
            bedIndex.getChromosomeListPositions(hashChr7, 0, 127481699)
        )
        assertEquals(
            listOf(0, 1, 2, 3, 4, 5, 6, 7, 8),
            bedIndex.getChromosomeListPositions(hashChr7, 0, 127481800)
        )
    }

    @Test
    fun `Test getChromosomeListPositions BedFile2`() {
        val bedIndex = BedIndexImpl()
        bedIndex.readFromBedFile(Path.of("test/resources/BedFile2.txt"))

        val hashChr11 = Objects.hashCode("chr11")

        assertEquals(
            listOf(0, 1, 2, 3, 4),
            bedIndex.getChromosomeListPositions(hashChr11, 0, 5255662)
        )

        assertEquals(
            listOf(0, 2, 4),
            bedIndex.getChromosomeListPositions(hashChr11, 5246919, 5248236)
        )

        assertEquals(
            listOf(1, 3, 4),
            bedIndex.getChromosomeListPositions(hashChr11, 5248234, 5255663)
        )
    }
}