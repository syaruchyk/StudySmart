package com.studysmart.core.data.db.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.studysmart.core.data.db.entities.EventJournalEntity;
import java.lang.Class;
import java.lang.Exception;
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
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class EventJournalDao_Impl implements EventJournalDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<EventJournalEntity> __insertionAdapterOfEventJournalEntity;

  public EventJournalDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfEventJournalEntity = new EntityInsertionAdapter<EventJournalEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `event_journal` (`id`,`streamId`,`eventType`,`payload`,`occurredAt`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EventJournalEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getStreamId());
        statement.bindString(3, entity.getEventType());
        statement.bindString(4, entity.getPayload());
        statement.bindLong(5, entity.getOccurredAt());
      }
    };
  }

  @Override
  public Object appendEvent(final EventJournalEntity event,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfEventJournalEntity.insertAndReturnId(event);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getStream(final String streamId,
      final Continuation<? super List<EventJournalEntity>> $completion) {
    final String _sql = "SELECT * FROM event_journal WHERE streamId = ? ORDER BY occurredAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, streamId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<EventJournalEntity>>() {
      @Override
      @NonNull
      public List<EventJournalEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfStreamId = CursorUtil.getColumnIndexOrThrow(_cursor, "streamId");
          final int _cursorIndexOfEventType = CursorUtil.getColumnIndexOrThrow(_cursor, "eventType");
          final int _cursorIndexOfPayload = CursorUtil.getColumnIndexOrThrow(_cursor, "payload");
          final int _cursorIndexOfOccurredAt = CursorUtil.getColumnIndexOrThrow(_cursor, "occurredAt");
          final List<EventJournalEntity> _result = new ArrayList<EventJournalEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EventJournalEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpStreamId;
            _tmpStreamId = _cursor.getString(_cursorIndexOfStreamId);
            final String _tmpEventType;
            _tmpEventType = _cursor.getString(_cursorIndexOfEventType);
            final String _tmpPayload;
            _tmpPayload = _cursor.getString(_cursorIndexOfPayload);
            final long _tmpOccurredAt;
            _tmpOccurredAt = _cursor.getLong(_cursorIndexOfOccurredAt);
            _item = new EventJournalEntity(_tmpId,_tmpStreamId,_tmpEventType,_tmpPayload,_tmpOccurredAt);
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

  @Override
  public Object getAllEvents(final Continuation<? super List<EventJournalEntity>> $completion) {
    final String _sql = "SELECT `event_journal`.`id` AS `id`, `event_journal`.`streamId` AS `streamId`, `event_journal`.`eventType` AS `eventType`, `event_journal`.`payload` AS `payload`, `event_journal`.`occurredAt` AS `occurredAt` FROM event_journal ORDER BY occurredAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<EventJournalEntity>>() {
      @Override
      @NonNull
      public List<EventJournalEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfStreamId = 1;
          final int _cursorIndexOfEventType = 2;
          final int _cursorIndexOfPayload = 3;
          final int _cursorIndexOfOccurredAt = 4;
          final List<EventJournalEntity> _result = new ArrayList<EventJournalEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EventJournalEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpStreamId;
            _tmpStreamId = _cursor.getString(_cursorIndexOfStreamId);
            final String _tmpEventType;
            _tmpEventType = _cursor.getString(_cursorIndexOfEventType);
            final String _tmpPayload;
            _tmpPayload = _cursor.getString(_cursorIndexOfPayload);
            final long _tmpOccurredAt;
            _tmpOccurredAt = _cursor.getLong(_cursorIndexOfOccurredAt);
            _item = new EventJournalEntity(_tmpId,_tmpStreamId,_tmpEventType,_tmpPayload,_tmpOccurredAt);
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
