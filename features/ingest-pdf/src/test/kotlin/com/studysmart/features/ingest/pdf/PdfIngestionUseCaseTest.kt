package com.studysmart.features.ingest.pdf

import android.content.Context
import android.net.Uri
import com.studysmart.core.data.db.AppDatabase
import com.studysmart.core.data.db.dao.DocumentDao
import com.studysmart.core.data.db.entities.ChunkEntity
import com.studysmart.core.data.db.entities.DocumentEntity
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class PdfIngestionUseCaseTest {

    private val context = mockk<Context>(relaxed = true)
    private val database = mockk<AppDatabase>()
    private val documentDao = mockk<DocumentDao>(relaxed = true)
    private val extractor = mockk<TextExtractor>()

    @Test
    fun `invoke extracts text and persists to database`() = runTest {
        // Setup
        val uri = Uri.parse("content://dummy/file.pdf")
        val docId = 99L
        
        // Mock DB
        every { database.documentDao() } returns documentDao
        coEvery { documentDao.insertDocument(any()) } returns docId
        coEvery { documentDao.insertChunks(any()) } just Runs
        
        // Mock Extractor
        val mockPages = listOf(
            PageContent(1, "Page 1 content"),
            PageContent(2, "Page 2 content")
        )
        val mockResult = ExtractionResult("Full Text", mockPages)
        coEvery { extractor.extract(uri) } returns mockResult

        // Mock Context/Resolver for filename (optional since we have fallback)
        // If we want to test filename resolution, we need to mock ContentResolver query
        // For now let's rely on fallback to lastPathSegment "file.pdf"
        
        val useCase = PdfIngestionUseCase(context, database, extractor)
        
        // Act
        val resultId = useCase.invoke(uri)

        // Assert
        assertEquals(docId, resultId)
        
        // Verify Document Insertion
        val docSlot = slot<DocumentEntity>()
        coVerify { documentDao.insertDocument(capture(docSlot)) }
        assertEquals("file.pdf", docSlot.captured.filename)
        assertEquals(uri.toString(), docSlot.captured.uri)
        
        // Verify Chunk Insertion
        val chunkSlot = slot<List<ChunkEntity>>()
        coVerify { documentDao.insertChunks(capture(chunkSlot)) }
        val chunks = chunkSlot.captured
        assertEquals(2, chunks.size)
        assertEquals("Page 1 content", chunks[0].content)
        assertEquals(1, chunks[0].pageNumber)
        assertEquals(docId, chunks[0].documentId)
    }
}
