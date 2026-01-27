package com.studysmart.core.data.db.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomDatabaseKt;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.studysmart.core.data.db.entities.ChunkEntity;
import com.studysmart.core.data.db.entities.DocumentEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class DocumentDao_Impl implements DocumentDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DocumentEntity> __insertionAdapterOfDocumentEntity;

  private final EntityInsertionAdapter<ChunkEntity> __insertionAdapterOfChunkEntity;

  public DocumentDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDocumentEntity = new EntityInsertionAdapter<DocumentEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `documents` (`id`,`filename`,`uri`,`importedAt`,`summary`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DocumentEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getFilename());
        statement.bindString(3, entity.getUri());
        statement.bindLong(4, entity.getImportedAt());
        if (entity.getSummary() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getSummary());
        }
      }
    };
    this.__insertionAdapterOfChunkEntity = new EntityInsertionAdapter<ChunkEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `chunks` (`id`,`documentId`,`content`,`pageNumber`,`startOffset`,`endOffset`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ChunkEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getDocumentId());
        statement.bindString(3, entity.getContent());
        statement.bindLong(4, entity.getPageNumber());
        if (entity.getStartOffset() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getStartOffset());
        }
        if (entity.getEndOffset() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getEndOffset());
        }
      }
    };
  }

  @Override
  public Object insertDocument(final DocumentEntity document,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfDocumentEntity.insertAndReturnId(document);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertChunks(final List<ChunkEntity> chunks,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfChunkEntity.insert(chunks);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertDocumentWithChunks(final DocumentEntity document,
      final List<ChunkEntity> chunks, final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> DocumentDao.DefaultImpls.insertDocumentWithChunks(DocumentDao_Impl.this, document, chunks, __cont), $completion);
  }

  @Override
  public Object getDocument(final long id, final Continuation<? super DocumentEntity> $completion) {
    final String _sql = "SELECT * FROM documents WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DocumentEntity>() {
      @Override
      @Nullable
      public DocumentEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFilename = CursorUtil.getColumnIndexOrThrow(_cursor, "filename");
          final int _cursorIndexOfUri = CursorUtil.getColumnIndexOrThrow(_cursor, "uri");
          final int _cursorIndexOfImportedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "importedAt");
          final int _cursorIndexOfSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "summary");
          final DocumentEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpFilename;
            _tmpFilename = _cursor.getString(_cursorIndexOfFilename);
            final String _tmpUri;
            _tmpUri = _cursor.getString(_cursorIndexOfUri);
            final long _tmpImportedAt;
            _tmpImportedAt = _cursor.getLong(_cursorIndexOfImportedAt);
            final String _tmpSummary;
            if (_cursor.isNull(_cursorIndexOfSummary)) {
              _tmpSummary = null;
            } else {
              _tmpSummary = _cursor.getString(_cursorIndexOfSummary);
            }
            _result = new DocumentEntity(_tmpId,_tmpFilename,_tmpUri,_tmpImportedAt,_tmpSummary);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getChunksForDocument(final long documentId,
      final Continuation<? super List<ChunkEntity>> $completion) {
    final String _sql = "SELECT * FROM chunks WHERE documentId = ? ORDER BY pageNumber ASC, startOffset ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, documentId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ChunkEntity>>() {
      @Override
      @NonNull
      public List<ChunkEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDocumentId = CursorUtil.getColumnIndexOrThrow(_cursor, "documentId");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfPageNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "pageNumber");
          final int _cursorIndexOfStartOffset = CursorUtil.getColumnIndexOrThrow(_cursor, "startOffset");
          final int _cursorIndexOfEndOffset = CursorUtil.getColumnIndexOrThrow(_cursor, "endOffset");
          final List<ChunkEntity> _result = new ArrayList<ChunkEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ChunkEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpDocumentId;
            _tmpDocumentId = _cursor.getLong(_cursorIndexOfDocumentId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final int _tmpPageNumber;
            _tmpPageNumber = _cursor.getInt(_cursorIndexOfPageNumber);
            final Integer _tmpStartOffset;
            if (_cursor.isNull(_cursorIndexOfStartOffset)) {
              _tmpStartOffset = null;
            } else {
              _tmpStartOffset = _cursor.getInt(_cursorIndexOfStartOffset);
            }
            final Integer _tmpEndOffset;
            if (_cursor.isNull(_cursorIndexOfEndOffset)) {
              _tmpEndOffset = null;
            } else {
              _tmpEndOffset = _cursor.getInt(_cursorIndexOfEndOffset);
            }
            _item = new ChunkEntity(_tmpId,_tmpDocumentId,_tmpContent,_tmpPageNumber,_tmpStartOffset,_tmpEndOffset);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
