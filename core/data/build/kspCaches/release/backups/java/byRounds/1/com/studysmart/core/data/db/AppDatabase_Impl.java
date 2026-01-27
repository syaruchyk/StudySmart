package com.studysmart.core.data.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.studysmart.core.data.db.dao.DocumentDao;
import com.studysmart.core.data.db.dao.DocumentDao_Impl;
import com.studysmart.core.data.db.dao.EventJournalDao;
import com.studysmart.core.data.db.dao.EventJournalDao_Impl;
import com.studysmart.core.data.db.dao.PracticeDao;
import com.studysmart.core.data.db.dao.PracticeDao_Impl;
import com.studysmart.core.data.db.dao.QuizDao;
import com.studysmart.core.data.db.dao.QuizDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile DocumentDao _documentDao;

  private volatile QuizDao _quizDao;

  private volatile PracticeDao _practiceDao;

  private volatile EventJournalDao _eventJournalDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `documents` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `filename` TEXT NOT NULL, `uri` TEXT NOT NULL, `importedAt` INTEGER NOT NULL, `summary` TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `chunks` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `documentId` INTEGER NOT NULL, `content` TEXT NOT NULL, `pageNumber` INTEGER NOT NULL, `startOffset` INTEGER, `endOffset` INTEGER, FOREIGN KEY(`documentId`) REFERENCES `documents`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_chunk_documentId` ON `chunks` (`documentId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `quizzes` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `documentId` INTEGER NOT NULL, `generatedAt` INTEGER NOT NULL, `title` TEXT NOT NULL, FOREIGN KEY(`documentId`) REFERENCES `documents`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_quiz_documentId` ON `quizzes` (`documentId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `questions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `quizId` INTEGER NOT NULL, `text` TEXT NOT NULL, `explanation` TEXT, FOREIGN KEY(`quizId`) REFERENCES `quizzes`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_question_quizId` ON `questions` (`quizId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `options` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `questionId` INTEGER NOT NULL, `text` TEXT NOT NULL, `isCorrect` INTEGER NOT NULL, FOREIGN KEY(`questionId`) REFERENCES `questions`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_option_questionId` ON `options` (`questionId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `sessions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `quizId` INTEGER NOT NULL, `startedAt` INTEGER NOT NULL, `completedAt` INTEGER, FOREIGN KEY(`quizId`) REFERENCES `quizzes`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_session_quizId` ON `sessions` (`quizId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `attempts` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sessionId` INTEGER NOT NULL, `questionId` INTEGER NOT NULL, `selectedOptionId` INTEGER, `isCorrect` INTEGER NOT NULL, `timeTakenMs` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL, FOREIGN KEY(`sessionId`) REFERENCES `sessions`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION , FOREIGN KEY(`questionId`) REFERENCES `questions`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_attempt_sessionId` ON `attempts` (`sessionId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_attempt_questionId` ON `attempts` (`questionId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `srs_states` (`questionId` INTEGER NOT NULL, `ease` REAL NOT NULL, `intervalDays` INTEGER NOT NULL, `repetitions` INTEGER NOT NULL, `dueAt` INTEGER NOT NULL, PRIMARY KEY(`questionId`), FOREIGN KEY(`questionId`) REFERENCES `questions`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_srs_dueAt` ON `srs_states` (`dueAt`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `event_journal` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `streamId` TEXT NOT NULL, `eventType` TEXT NOT NULL, `payload` TEXT NOT NULL, `occurredAt` INTEGER NOT NULL)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_journal_streamId_occurredAt` ON `event_journal` (`streamId`, `occurredAt`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `session_snapshots` (`sessionId` INTEGER NOT NULL, `snapshotData` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`sessionId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '687a64c78d52ca59d4dd1ccbe1a64fd0')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `documents`");
        db.execSQL("DROP TABLE IF EXISTS `chunks`");
        db.execSQL("DROP TABLE IF EXISTS `quizzes`");
        db.execSQL("DROP TABLE IF EXISTS `questions`");
        db.execSQL("DROP TABLE IF EXISTS `options`");
        db.execSQL("DROP TABLE IF EXISTS `sessions`");
        db.execSQL("DROP TABLE IF EXISTS `attempts`");
        db.execSQL("DROP TABLE IF EXISTS `srs_states`");
        db.execSQL("DROP TABLE IF EXISTS `event_journal`");
        db.execSQL("DROP TABLE IF EXISTS `session_snapshots`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsDocuments = new HashMap<String, TableInfo.Column>(5);
        _columnsDocuments.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDocuments.put("filename", new TableInfo.Column("filename", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDocuments.put("uri", new TableInfo.Column("uri", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDocuments.put("importedAt", new TableInfo.Column("importedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDocuments.put("summary", new TableInfo.Column("summary", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDocuments = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDocuments = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDocuments = new TableInfo("documents", _columnsDocuments, _foreignKeysDocuments, _indicesDocuments);
        final TableInfo _existingDocuments = TableInfo.read(db, "documents");
        if (!_infoDocuments.equals(_existingDocuments)) {
          return new RoomOpenHelper.ValidationResult(false, "documents(com.studysmart.core.data.db.entities.DocumentEntity).\n"
                  + " Expected:\n" + _infoDocuments + "\n"
                  + " Found:\n" + _existingDocuments);
        }
        final HashMap<String, TableInfo.Column> _columnsChunks = new HashMap<String, TableInfo.Column>(6);
        _columnsChunks.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChunks.put("documentId", new TableInfo.Column("documentId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChunks.put("content", new TableInfo.Column("content", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChunks.put("pageNumber", new TableInfo.Column("pageNumber", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChunks.put("startOffset", new TableInfo.Column("startOffset", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChunks.put("endOffset", new TableInfo.Column("endOffset", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysChunks = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysChunks.add(new TableInfo.ForeignKey("documents", "CASCADE", "NO ACTION", Arrays.asList("documentId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesChunks = new HashSet<TableInfo.Index>(1);
        _indicesChunks.add(new TableInfo.Index("index_chunk_documentId", false, Arrays.asList("documentId"), Arrays.asList("ASC")));
        final TableInfo _infoChunks = new TableInfo("chunks", _columnsChunks, _foreignKeysChunks, _indicesChunks);
        final TableInfo _existingChunks = TableInfo.read(db, "chunks");
        if (!_infoChunks.equals(_existingChunks)) {
          return new RoomOpenHelper.ValidationResult(false, "chunks(com.studysmart.core.data.db.entities.ChunkEntity).\n"
                  + " Expected:\n" + _infoChunks + "\n"
                  + " Found:\n" + _existingChunks);
        }
        final HashMap<String, TableInfo.Column> _columnsQuizzes = new HashMap<String, TableInfo.Column>(4);
        _columnsQuizzes.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuizzes.put("documentId", new TableInfo.Column("documentId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuizzes.put("generatedAt", new TableInfo.Column("generatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuizzes.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysQuizzes = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysQuizzes.add(new TableInfo.ForeignKey("documents", "CASCADE", "NO ACTION", Arrays.asList("documentId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesQuizzes = new HashSet<TableInfo.Index>(1);
        _indicesQuizzes.add(new TableInfo.Index("index_quiz_documentId", false, Arrays.asList("documentId"), Arrays.asList("ASC")));
        final TableInfo _infoQuizzes = new TableInfo("quizzes", _columnsQuizzes, _foreignKeysQuizzes, _indicesQuizzes);
        final TableInfo _existingQuizzes = TableInfo.read(db, "quizzes");
        if (!_infoQuizzes.equals(_existingQuizzes)) {
          return new RoomOpenHelper.ValidationResult(false, "quizzes(com.studysmart.core.data.db.entities.QuizEntity).\n"
                  + " Expected:\n" + _infoQuizzes + "\n"
                  + " Found:\n" + _existingQuizzes);
        }
        final HashMap<String, TableInfo.Column> _columnsQuestions = new HashMap<String, TableInfo.Column>(4);
        _columnsQuestions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("quizId", new TableInfo.Column("quizId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("text", new TableInfo.Column("text", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("explanation", new TableInfo.Column("explanation", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysQuestions = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysQuestions.add(new TableInfo.ForeignKey("quizzes", "CASCADE", "NO ACTION", Arrays.asList("quizId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesQuestions = new HashSet<TableInfo.Index>(1);
        _indicesQuestions.add(new TableInfo.Index("index_question_quizId", false, Arrays.asList("quizId"), Arrays.asList("ASC")));
        final TableInfo _infoQuestions = new TableInfo("questions", _columnsQuestions, _foreignKeysQuestions, _indicesQuestions);
        final TableInfo _existingQuestions = TableInfo.read(db, "questions");
        if (!_infoQuestions.equals(_existingQuestions)) {
          return new RoomOpenHelper.ValidationResult(false, "questions(com.studysmart.core.data.db.entities.QuestionEntity).\n"
                  + " Expected:\n" + _infoQuestions + "\n"
                  + " Found:\n" + _existingQuestions);
        }
        final HashMap<String, TableInfo.Column> _columnsOptions = new HashMap<String, TableInfo.Column>(4);
        _columnsOptions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOptions.put("questionId", new TableInfo.Column("questionId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOptions.put("text", new TableInfo.Column("text", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOptions.put("isCorrect", new TableInfo.Column("isCorrect", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysOptions = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysOptions.add(new TableInfo.ForeignKey("questions", "CASCADE", "NO ACTION", Arrays.asList("questionId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesOptions = new HashSet<TableInfo.Index>(1);
        _indicesOptions.add(new TableInfo.Index("index_option_questionId", false, Arrays.asList("questionId"), Arrays.asList("ASC")));
        final TableInfo _infoOptions = new TableInfo("options", _columnsOptions, _foreignKeysOptions, _indicesOptions);
        final TableInfo _existingOptions = TableInfo.read(db, "options");
        if (!_infoOptions.equals(_existingOptions)) {
          return new RoomOpenHelper.ValidationResult(false, "options(com.studysmart.core.data.db.entities.OptionEntity).\n"
                  + " Expected:\n" + _infoOptions + "\n"
                  + " Found:\n" + _existingOptions);
        }
        final HashMap<String, TableInfo.Column> _columnsSessions = new HashMap<String, TableInfo.Column>(4);
        _columnsSessions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessions.put("quizId", new TableInfo.Column("quizId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessions.put("startedAt", new TableInfo.Column("startedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessions.put("completedAt", new TableInfo.Column("completedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSessions = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysSessions.add(new TableInfo.ForeignKey("quizzes", "NO ACTION", "NO ACTION", Arrays.asList("quizId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesSessions = new HashSet<TableInfo.Index>(1);
        _indicesSessions.add(new TableInfo.Index("index_session_quizId", false, Arrays.asList("quizId"), Arrays.asList("ASC")));
        final TableInfo _infoSessions = new TableInfo("sessions", _columnsSessions, _foreignKeysSessions, _indicesSessions);
        final TableInfo _existingSessions = TableInfo.read(db, "sessions");
        if (!_infoSessions.equals(_existingSessions)) {
          return new RoomOpenHelper.ValidationResult(false, "sessions(com.studysmart.core.data.db.entities.SessionEntity).\n"
                  + " Expected:\n" + _infoSessions + "\n"
                  + " Found:\n" + _existingSessions);
        }
        final HashMap<String, TableInfo.Column> _columnsAttempts = new HashMap<String, TableInfo.Column>(7);
        _columnsAttempts.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAttempts.put("sessionId", new TableInfo.Column("sessionId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAttempts.put("questionId", new TableInfo.Column("questionId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAttempts.put("selectedOptionId", new TableInfo.Column("selectedOptionId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAttempts.put("isCorrect", new TableInfo.Column("isCorrect", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAttempts.put("timeTakenMs", new TableInfo.Column("timeTakenMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAttempts.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAttempts = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysAttempts.add(new TableInfo.ForeignKey("sessions", "NO ACTION", "NO ACTION", Arrays.asList("sessionId"), Arrays.asList("id")));
        _foreignKeysAttempts.add(new TableInfo.ForeignKey("questions", "NO ACTION", "NO ACTION", Arrays.asList("questionId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesAttempts = new HashSet<TableInfo.Index>(2);
        _indicesAttempts.add(new TableInfo.Index("index_attempt_sessionId", false, Arrays.asList("sessionId"), Arrays.asList("ASC")));
        _indicesAttempts.add(new TableInfo.Index("index_attempt_questionId", false, Arrays.asList("questionId"), Arrays.asList("ASC")));
        final TableInfo _infoAttempts = new TableInfo("attempts", _columnsAttempts, _foreignKeysAttempts, _indicesAttempts);
        final TableInfo _existingAttempts = TableInfo.read(db, "attempts");
        if (!_infoAttempts.equals(_existingAttempts)) {
          return new RoomOpenHelper.ValidationResult(false, "attempts(com.studysmart.core.data.db.entities.AttemptEntity).\n"
                  + " Expected:\n" + _infoAttempts + "\n"
                  + " Found:\n" + _existingAttempts);
        }
        final HashMap<String, TableInfo.Column> _columnsSrsStates = new HashMap<String, TableInfo.Column>(5);
        _columnsSrsStates.put("questionId", new TableInfo.Column("questionId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSrsStates.put("ease", new TableInfo.Column("ease", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSrsStates.put("intervalDays", new TableInfo.Column("intervalDays", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSrsStates.put("repetitions", new TableInfo.Column("repetitions", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSrsStates.put("dueAt", new TableInfo.Column("dueAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSrsStates = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysSrsStates.add(new TableInfo.ForeignKey("questions", "NO ACTION", "NO ACTION", Arrays.asList("questionId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesSrsStates = new HashSet<TableInfo.Index>(1);
        _indicesSrsStates.add(new TableInfo.Index("index_srs_dueAt", false, Arrays.asList("dueAt"), Arrays.asList("ASC")));
        final TableInfo _infoSrsStates = new TableInfo("srs_states", _columnsSrsStates, _foreignKeysSrsStates, _indicesSrsStates);
        final TableInfo _existingSrsStates = TableInfo.read(db, "srs_states");
        if (!_infoSrsStates.equals(_existingSrsStates)) {
          return new RoomOpenHelper.ValidationResult(false, "srs_states(com.studysmart.core.data.db.entities.SrsStateEntity).\n"
                  + " Expected:\n" + _infoSrsStates + "\n"
                  + " Found:\n" + _existingSrsStates);
        }
        final HashMap<String, TableInfo.Column> _columnsEventJournal = new HashMap<String, TableInfo.Column>(5);
        _columnsEventJournal.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEventJournal.put("streamId", new TableInfo.Column("streamId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEventJournal.put("eventType", new TableInfo.Column("eventType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEventJournal.put("payload", new TableInfo.Column("payload", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEventJournal.put("occurredAt", new TableInfo.Column("occurredAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysEventJournal = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesEventJournal = new HashSet<TableInfo.Index>(1);
        _indicesEventJournal.add(new TableInfo.Index("index_journal_streamId_occurredAt", false, Arrays.asList("streamId", "occurredAt"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoEventJournal = new TableInfo("event_journal", _columnsEventJournal, _foreignKeysEventJournal, _indicesEventJournal);
        final TableInfo _existingEventJournal = TableInfo.read(db, "event_journal");
        if (!_infoEventJournal.equals(_existingEventJournal)) {
          return new RoomOpenHelper.ValidationResult(false, "event_journal(com.studysmart.core.data.db.entities.EventJournalEntity).\n"
                  + " Expected:\n" + _infoEventJournal + "\n"
                  + " Found:\n" + _existingEventJournal);
        }
        final HashMap<String, TableInfo.Column> _columnsSessionSnapshots = new HashMap<String, TableInfo.Column>(3);
        _columnsSessionSnapshots.put("sessionId", new TableInfo.Column("sessionId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessionSnapshots.put("snapshotData", new TableInfo.Column("snapshotData", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessionSnapshots.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSessionSnapshots = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSessionSnapshots = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSessionSnapshots = new TableInfo("session_snapshots", _columnsSessionSnapshots, _foreignKeysSessionSnapshots, _indicesSessionSnapshots);
        final TableInfo _existingSessionSnapshots = TableInfo.read(db, "session_snapshots");
        if (!_infoSessionSnapshots.equals(_existingSessionSnapshots)) {
          return new RoomOpenHelper.ValidationResult(false, "session_snapshots(com.studysmart.core.data.db.entities.SessionSnapshotEntity).\n"
                  + " Expected:\n" + _infoSessionSnapshots + "\n"
                  + " Found:\n" + _existingSessionSnapshots);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "687a64c78d52ca59d4dd1ccbe1a64fd0", "a462d49f4ae0ce3f3648f4554fcf0a30");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "documents","chunks","quizzes","questions","options","sessions","attempts","srs_states","event_journal","session_snapshots");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `documents`");
      _db.execSQL("DELETE FROM `chunks`");
      _db.execSQL("DELETE FROM `quizzes`");
      _db.execSQL("DELETE FROM `attempts`");
      _db.execSQL("DELETE FROM `questions`");
      _db.execSQL("DELETE FROM `options`");
      _db.execSQL("DELETE FROM `sessions`");
      _db.execSQL("DELETE FROM `srs_states`");
      _db.execSQL("DELETE FROM `event_journal`");
      _db.execSQL("DELETE FROM `session_snapshots`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(DocumentDao.class, DocumentDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(QuizDao.class, QuizDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PracticeDao.class, PracticeDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(EventJournalDao.class, EventJournalDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public DocumentDao documentDao() {
    if (_documentDao != null) {
      return _documentDao;
    } else {
      synchronized(this) {
        if(_documentDao == null) {
          _documentDao = new DocumentDao_Impl(this);
        }
        return _documentDao;
      }
    }
  }

  @Override
  public QuizDao quizDao() {
    if (_quizDao != null) {
      return _quizDao;
    } else {
      synchronized(this) {
        if(_quizDao == null) {
          _quizDao = new QuizDao_Impl(this);
        }
        return _quizDao;
      }
    }
  }

  @Override
  public PracticeDao practiceDao() {
    if (_practiceDao != null) {
      return _practiceDao;
    } else {
      synchronized(this) {
        if(_practiceDao == null) {
          _practiceDao = new PracticeDao_Impl(this);
        }
        return _practiceDao;
      }
    }
  }

  @Override
  public EventJournalDao eventJournalDao() {
    if (_eventJournalDao != null) {
      return _eventJournalDao;
    } else {
      synchronized(this) {
        if(_eventJournalDao == null) {
          _eventJournalDao = new EventJournalDao_Impl(this);
        }
        return _eventJournalDao;
      }
    }
  }
}
