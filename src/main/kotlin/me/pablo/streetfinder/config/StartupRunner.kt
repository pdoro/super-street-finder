package me.pablo.streetfinder.config

import logger
import me.pablo.streetfinder.Application
import me.pablo.streetfinder.infrastructure.secundary.ner.NERTrainer
import me.pablo.streetfinder.infrastructure.secundary.search.model.StreetEntity
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.core.io.ClassPathResource
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.document.Document
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.security.SecureRandom
import kotlin.system.measureTimeMillis

@Component
class StartupRunner(
    private val appConfig: AppConfig,
    private val nerTrainer: NERTrainer,
    private val elasticsearchOperations: ElasticsearchOperations
) {
    val log = logger(javaClass)
    val index = IndexCoordinates.of("streets")
    val rnd = SecureRandom()

    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() {

        val dataset = loadDataset()

        if (!nerTrainer.modelExists()) {
            val millis = measureTimeMillis {
                log.info("No model found. Training new model")
                prepareTrainFile(dataset)
                nerTrainer.train()
            }
            log.info("Training model finished in $millis ms")
        }

        val indexExists = elasticsearchOperations.indexOps(index).exists()

        if (!indexExists) {
            val millis = measureTimeMillis {

                log.info("No index found. Indexing streets")

                val mappingResource = ClassPathResource("mapping.json")
                val mappingJson = Files.readString(mappingResource.file.toPath())
                val settingsResource = ClassPathResource("settings.json")
                val settingsJson = Files.readString(settingsResource.file.toPath())

                elasticsearchOperations.indexOps(index).create(Document.parse(settingsJson))
                elasticsearchOperations.indexOps(index).putMapping(Document.parse(mappingJson))

                dataset.map {
                    IndexQueryBuilder().withObject(it).build()
                }.apply {
                    elasticsearchOperations.bulkIndex(this.toList(), index)
                }
            }
            log.info("Indexing finished in $millis ms")
        }
    }

    private fun prepareTrainFile(dataset: Sequence<StreetEntity>) {

        val bw = Files.newBufferedWriter(appConfig.trainFile)

        dataset.forEachIndexed { idx, street ->
            val strBuilder: StringBuilder = StringBuilder()

            street.type.split(' ').forEach {
                strBuilder.appendLine("$it street_type")
            }
            if (street.nexus.isNotBlank()) {
                street.nexus.split(' ').forEach {
                    strBuilder.appendLine("$it street_name_nexus")
                }
            }
            street.name.split(' ').forEach {
                strBuilder.appendLine("$it street_name")
            }

            if (rnd.nextFloat() > 0.5)
                strBuilder.appendLine(", separator")
            if (rnd.nextFloat() > 0.5)
                strBuilder.appendLine("${street.number} street_number")
            if (rnd.nextFloat() > 0.5)
                strBuilder.appendLine("${street.postalCode} postal_code")
            if (rnd.nextFloat() > 0.5)
                strBuilder.appendLine("${street.country} country")

            bw.appendLine(strBuilder.toString())

            if (idx % 100 == 0)
                log.info("Written $idx rows to train dataset")
        }

        bw.close()
    }

    private fun loadDataset(): Sequence<StreetEntity> {
        val excelFile = Files.newInputStream(appConfig.rawDatasetFile)
        val workbook = XSSFWorkbook(excelFile)

        val sheet = workbook.getSheet("Hoja")
        val dataset = sheet.asSequence()
            .drop(1) // drop header
            .map {
                StreetEntity(
                    type = it.getCell(1).toString().toLowerCase().capitalize(),
                    nexus = it.getCell(2)?.toString()?.toLowerCase() ?: "",
                    name = it.getCell(3).toString().toLowerCase().capitalize(),
                    number = rnd.nextInt(100),
                    postalCode = 28_000 + rnd.nextInt(999),
                    country = "España"
                )
            }

        workbook.close()
        excelFile.close()

        return dataset
    }
}