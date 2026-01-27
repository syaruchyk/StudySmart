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
import com.studysmart.core.data.db.entities.AttemptEntity;
import com.studysmart.core.data.db.entities.QuestionEntity;
import com.studysmart.core.data.db.entities.SessionEntity;
import com.studysmart.core.data.db.entities.SrsStateEntity;
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
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class PracticeDao_Impl implements PracticeDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SessionEntity> __insertionAdapterOfSessionEntity;

  private final EntityInsertionAdapter<AttemptEntity> __insertionAdapterOfAttemptEntity;

  private final EntityInsertionAdapter<SrsStateEntity> __insertionAdapterOfSrsStateEntity;

  public PracticeDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSessionEntity = new EntityInsertionAdapter<SessionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `sessions` (`id`,`quizId`,`startedAt`,`completedAt`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SessionEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getQuizId());
        statement.bindLong(3, entity.getStartedAt());
        if (entity.getCompletedAt() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getCompletedAt());
        }
      }
    };
    this.__insertionAdapterOfAttemptEntity = new EntityInsertionAdapter<AttemptEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `attempts` (`id`,`sessionId`,`questionId`,`selectedOptionId`,`isCorrect`,`timeTakenMs`,`timestamp`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AttemptEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getSessionId());
        statement.bindLong(3, entity.getQuestionId());
        if (entity.getSelectedOptionId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getSelectedOptionId());
        }
        final int _tmp = entity.isCorrect() ? 1 : 0;
        statement.bindLong(5, _tmp);
        statement.bindLong(6, entity.getTimeTakenMs());
        statement.bindLong(7, entity.getTimestamp());
      }
    };
    this.__insertionAdapterOfSrsStateEntity = new EntityInsertionAdapter<SrsStateEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `srs_states` (`questionId`,`ease`,`intervalDays`,`repetitions`,`dueAt`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SrsStateEntity entity) {
        statement.bindLong(1, entity.getQuestionId());
        statement.bindDouble(2, entity.getEase());
        statement.bindLong(3, entity.getIntervalDays());
        statement.bindLong(4, entity.getRepetitions());
        statement.bindLong(5, entity.getDueAt());
      }
    };
  }

  @Override
  public Object insertSession(final SessionEntity session,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfSessionEntity.insertAndReturnId(session);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAttempt(final AttemptEntity attempt,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfAttemptEntity.insertAndReturnId(attempt);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertSrsState(final SrsStateEntity srsState,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSrsStateEntity.insert(srsState);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getDueQuestions(final long now,
      final Continuation<? super List<QuestionEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT q.* FROM questions q\n"
            + "        INNER JOIN srs_states s ON q.id = s.questionId\n"
            + "        WHERE s.dueAt <= ?\n"
            + "        ORDER BY s.dueAt ASC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, now);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<QuestionEntity>>() {
      @Override
      @NonNull
      public List<QuestionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfQuizId = CursorUtil.getColumnIndexOrThrow(_cursor, "quizId");
          final int _cursorIndexOfText = CursorUtil.getColumnIndexOrThrow(_cursor, "text");
          final int _cursorIndexOfExplanation = CursorUtil.getColumnIndexOrThrow(_cursor, "explanation");
          final List<QuestionEntity> _result = new ArrayList<QuestionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final QuestionEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpQuizId;
            _tmpQuizId = _cursor.getLong(_cursorIndexOfQuizId);
            final String _tmpText;
            _tmpText = _cursor.getString(_cursorIndexOfText);
            final String _tmpExplanation;
            if (_cursor.isNull(_cursorIndexOfExplanation)) {
              _tmpExplanation = null;
            } else {
              _tmpExplanation = _cursor.getString(_cursorIndexOfExplanation);
            }
            _item = new QuestionEntity(_tmpId,_tmpQuizId,_tmpText,_tmpExplanation);
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
  public Object getRecentFailures(final int limit,
      final Continuation<? super List<AttemptEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM attempts \n"
            + "        WHERE isCorrect = 0 \n"
            + "        ORDER BY timestamp DESC \n"
            + "        LIMIT ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AttemptEntity>>() {
      @Override
      @NonNull
      public List<AttemptEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionId");
          final int _cursorIndexOfQuestionId = CursorUtil.getColumnIndexOrThrow(_cursor, "questionId");
          final int _cursorIndexOfSelectedOptionId = CursorUtil.getColumnIndexOrThrow(_cursor, "selectedOptionId");
          final int _cursorIndexOfIsCorrect = CursorUtil.getColumnIndexOrThrow(_cursor, "isCorrect");
          final int _cursorIndexOfTimeTakenMs = CursorUtil.getColumnIndexOrThrow(_cursor, "timeTakenMs");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<AttemptEntity> _result = new ArrayList<AttemptEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AttemptEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpSessionId;
            _tmpSessionId = _cursor.getLong(_cursorIndexOfSessionId);
            final long _tmpQuestionId;
            _tmpQuestionId = _cursor.getLong(_cursorIndexOfQuestionId);
            final Long _tmpSelectedOptionId;
            if (_cursor.isNull(_cursorIndexOfSelectedOptionId)) {
              _tmpSelectedOptionId = null;
            } else {
              _tmpSelectedOptionId = _cursor.getLong(_cursorIndexOfSelectedOptionId);
            }
            final boolean _tmpIsCorrect;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCorrect);
            _tmpIsCorrect = _tmp != 0;
            final long _tmpTimeTakenMs;
            _tmpTimeTakenMs = _cursor.getLong(_cursorIndexOfTimeTakenMs);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new AttemptEntity(_tmpId,_tmpSessionId,_tmpQuestionId,_tmpSelectedOptionId,_tmpIsCorrect,_tmpTimeTakenMs,_tmpTimestamp);
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
  public Object getStatsByTopic(final Continuation<? super List<TopicStats>> $completion) {
    final String _sql = "\n"
            + "        SELECT q.quizId as topicId, COUNT(*) as totalAttempts, SUM(CASE WHEN a.isCorrect THEN 1 ELSE 0 END) as correctAttempts\n"
            + "        FROM attempts a\n"
            + "        INNER JOIN questions q ON a.questionId = q.id\n"
            + "        GROUP BY q.quizId\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TopicStats>>() {
      @Override
      @NonNull
      public List<TopicStats> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfTopicId = 0;
          final int _cursorIndexOfTotalAttempts = 1;
          final int _cursorIndexOfCorrectAttempts = 2;
          final List<TopicStats> _result = new ArrayList<TopicStats>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TopicStats _item;
            final long _tmpTopicId;
            _tmpTopicId = _cursor.getLong(_cursorIndexOfTopicId);
            final int _tmpTotalAttempts;
            _tmpTotalAttempts = _cursor.getInt(_cursorIndexOfTotalAttempts);
            final int _tmpCorrectAttempts;
            _tmpCorrectAttempts = _cursor.getInt(_cursorIndexOfCorrectAttempts);
            _item = new TopicStats(_tmpTopicId,_tmpTotalAttempts,_tmpCorrectAttempts);
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
