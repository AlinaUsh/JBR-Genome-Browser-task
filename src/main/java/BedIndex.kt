import java.lang.IllegalArgumentException
import java.nio.file.Path

interface BedIndex {
    /**
     * Prints the content of [BedIndex] for debug
     */
    fun printBedIndexImpl()

    /**
     * Gets the content of [BedIndex]
     */
    fun getHashTable(): HashMap<Int, MutableList<BedIndexEntry>>

    /**
     * Returns hash of [BedIndexEntry] if  [BedIndexEntry] is given
     */
    fun getBedIndexEntryHash(entry: BedIndexEntry): Int

    /**
     * Returns hash of [BedIndexEntry] if only [chromosome] is given
     */
    fun getBedIndexEntryHash(chromosome: String): Int

    /**
     * Formates [BedIndexEntry] to print it to the output file
     */
    fun formatBedIndexEntryForOutput(entry: BedIndexEntry): String

    /**
     * Adds new [BedIndexEntry] element to hash table
     */
    fun addElement(entry: BedIndexEntry)

    /**
     * Writes [BedIndex] to file [indexPath]
     */
    fun writeToFile(indexPath: Path)

    /**
     * Reads data and creates [BedIndex] for BED file[bedPath]
     *
     * Throws [IllegalArgumentException] if [bedPath] is not found
     * or block has incorrect ends
     * or line has incorrect number of arguments
     */
    fun readFromBedFile(bedPath: Path)

    /**
     * Reads data and creates [BedIndex] Index file from [indexPath]
     *
     * Throws [IllegalArgumentException] if [indexPath] is not found
     * or block has incorrect ends
     * or line has incorrect number of arguments
     */
    fun readFromIndexFile(indexPath: Path)

    /**
     * Parses String to get [BedEntry]
     *
     * Throws [IllegalArgumentException] if line has incorrect number of arguments
     */
    fun parseStringToBedEntry(line: String): BedEntry

    /**
     * For each chromosome in [BedIndex] sorts [BedIndexEntry]
     * by chromStart. If blocks starts are same than sorts by chromEnd.
     * And if chromEnd are also same sorts by position in BED file.
     */
    fun sortElementsInBedIndex()

    /**
     * Binary search in the list of current chromosome.
     * Finds the first [BedEntry] for which [BedIndexEntry.start]
     * is not less than [chromStart]
     *
     * Throws [IllegalArgumentException] if there is no such chromosome
     * in [BedIndex]
     */
    fun binarySearchInBedIndex(hash: Int, chromStart: Int): Int

    /**
     * Returns sorted list of positions in BED file of chromosomes
     * if their [BedIndexEntry.start] >= [chromStart] (inclusive) and
     * [BedIndexEntry.end] < [chromEnd] (exclusive)
     */
    fun getChromosomeListPositions(hash: Int, chromStart: Int, chromEnd: Int): List<Int>
}