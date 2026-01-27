package com.studysmart.core.data.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.studysmart.core.data.db.entities.DocumentEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private lateinit var db: AppDatabase
    private lateinit var context: Context

    @Before
    fun createDb() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun testDatabaseCreationAndInsert() = runTest {
        val documentDao = db.documentDao()
        val doc = DocumentEntity(filename = "test.pdf", importedAt = System.currentTimeMillis(), summary = "test")
        
        val id = documentDao.insertDocument(doc)
        val retrieved = documentDao.getDocument(id)
        
        assertNotNull(retrieved)
        assertEquals("test.pdf", retrieved?.filename)
    }
}
