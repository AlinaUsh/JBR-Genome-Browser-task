import java.nio.file.Path

class BedReaderImpl : BedReader {

    override fun createIndex(bedPath: Path, indexPath: Path) {
        var bedIndex: BedIndex = BedIndexImpl()

        bedIndex.readFromBedFile(bedPath)
        bedIndex.writeToFile(indexPath)
    }

    override fun loadIndex(indexPath: Path): BedIndex {
        var bedIndex: BedIndex = BedIndexImpl()
        bedIndex.readFromIndexFile(indexPath)
        return bedIndex
    }

    override fun findWithIndex(
        index: BedIndex,
        bedPath: Path,
        chromosome: String,
        start: Int,
        end: Int
    ): List<BedEntry> {

        var list: MutableList<BedEntry> = mutableListOf()
        val chromosomeHash = index.getBedIndexEntryHash(chromosome)
        val positionList = index.getChromosomeListPositions(chromosomeHash, start, end)

        var lineNubmer = 0
        var position = 0

        for (line in bedPath.toFile().readLines()) {
            if (position >= positionList.size) {
                break;
            }
            if (lineNubmer == positionList[position]) {
                list.add(index.parseStringToBedEntry(line))
                position++
            }
            lineNubmer++
        }
        return list
    }
}