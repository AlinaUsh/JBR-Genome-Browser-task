import java.lang.IllegalArgumentException
import java.nio.file.Files
import java.nio.file.Path
import java.util.Objects
import kotlin.collections.HashMap

class BedIndexImpl : BedIndex {

    private var hashTable: HashMap<Int, MutableList<BedIndexEntry>> = HashMap();

    override fun printBedIndexImpl() {
        for (entry in hashTable) {
            println("${entry.key}\t${entry.value}")
        }
    }

    override fun getHashTable(): HashMap<Int, MutableList<BedIndexEntry>> {
        return hashTable
    }

    override fun sortElementsInBedIndex() {
        for (mapEntry in hashTable) {
            mapEntry.value.sortWith(compareBy({ it.start }, { it.end }, { it.position }))
        }
    }

    override fun binarySearchInBedIndex(hash: Int, chromStart: Int): Int {
        if (hashTable[hash] == null) {
            throw IllegalArgumentException("No such chromosome")
        }
        var l = 0
        val listSize = hashTable[hash]!!.size
        var r = listSize
        while (r - l > 1) {
            val m = (l + r) / 2
            if (hashTable[hash]!!.get(m).start < chromStart) {
                l = m;
            } else {
                r = m;
            }
        }
        while (l < listSize && hashTable[hash]!!.get(l).start < chromStart) {
            l += 1;
        }
        return l;
    }

    override fun getChromosomeListPositions(hash: Int, chromStart: Int, chromEnd: Int): List<Int> {
        var list: MutableList<Int> = mutableListOf()
        var i = binarySearchInBedIndex(hash, chromStart)

        val listSize = hashTable[hash]!!.size

        while (i < listSize && hashTable[hash]!![i].end < chromEnd) {
            list.add(hashTable[hash]!![i].position)
            i += 1
        }
        return list.sorted()
    }


    override fun getBedIndexEntryHash(entry: BedIndexEntry): Int {
        return Objects.hashCode(entry.chromosome)
    }

    override fun getBedIndexEntryHash(chromosome: String): Int {
        return Objects.hashCode(chromosome)
    }

    override fun formatBedIndexEntryForOutput(entry: BedIndexEntry): String {
        var output = "${entry.chromosome}\t${entry.start}\t${entry.end}\t${entry.position}"
        return output
    }

    override fun addElement(entry: BedIndexEntry) {
        var hash = getBedIndexEntryHash(entry)
        if (hashTable[hash] == null) {
            hashTable[hash] = mutableListOf(entry)
            return
        }
        hashTable[hash]?.add(entry)
    }

    override fun writeToFile(indexPath: Path) {
        val writer = indexPath.toFile().bufferedWriter();

        for (mapEntry in hashTable.entries) {
            for (entry in mapEntry.value) {
                writer.write("${mapEntry.key}\t" + formatBedIndexEntryForOutput(entry))
                writer.newLine()
            }
        }
        writer.close()
    }

    override fun readFromBedFile(bedPath: Path) {
        if (!Files.exists(bedPath)) {
            throw IllegalArgumentException("No file $bedPath")
        }

        var lineNubmer = 0
        for (line in bedPath.toFile().readLines()) {
            val list: List<Any> = line.split("\t")

            if (list.size < 3) {
                throw IllegalArgumentException("Not enough arguments in line $lineNubmer:\n$line")
            }

            if (list.size > 12) {
                throw IllegalArgumentException("Too many arguments in line $lineNubmer:\n$line")
            }

            val chromStart = Integer.parseInt(list.get(1) as String)
            val chromEnd = Integer.parseInt(list.get(2) as String)

            if (chromEnd <= chromStart) {
                throw IllegalArgumentException("Incorrect block in line $lineNubmer:\n$line")
            }

            val newEntry = BedIndexEntry(
                list.get(0) as String, chromStart, chromEnd, lineNubmer
            )
            this.addElement(newEntry)
            lineNubmer++
        }
        sortElementsInBedIndex()
    }

    override fun readFromIndexFile(indexPath: Path) {
        if (!Files.exists(indexPath)) {
            throw IllegalArgumentException("No file $indexPath")
        }

        var lineNubmer = 0
        for (line in indexPath.toFile().readLines()) {
            val list: List<Any> = line.split("\t")

            if (list.size < 5) {
                throw IllegalArgumentException("Not enough arguments in line $lineNubmer:\n$line")
            }

            val chromStart = Integer.parseInt(list.get(2) as String)
            val chromEnd = Integer.parseInt(list.get(3) as String)
            val chromPosition = Integer.parseInt(list.get(4) as String)

            if (chromEnd <= chromStart) {
                throw IllegalArgumentException("Incorrect block in line $lineNubmer:\n$line")
            }

            if (chromPosition < 0) {
                throw IllegalArgumentException("Incorrect position in line $lineNubmer:\n$line")
            }

            val newEntry = BedIndexEntry(
                list.get(1) as String, chromStart, chromEnd, chromPosition
            )
            this.addElement(newEntry)
            lineNubmer++
        }
    }

    override fun parseStringToBedEntry(line: String): BedEntry {
        val list: List<Any> = line.split("\t")

        if (list.size < 3) {
            throw IllegalArgumentException("Not enough arguments in line:\n$line")
        }

        if (list.size > 12) {
            throw IllegalArgumentException("Too many arguments in line:\n$line")
        }

        val entry = BedEntry(
            list.get(0) as String, Integer.parseInt(list.get(1) as String),
            Integer.parseInt(list.get(2) as String), list.subList(3, list.size)
        )
        return entry
    }
}