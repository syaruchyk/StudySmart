
tech: android (kotlin), minsdk 26, room for local persistence.

architecture: meces modules + agents: session, pdf, quizgen, srs, storage, insights. each agent edits only its module.

pdf pipeline: extract embedded text first; fallback ocr only when needed.

quiz generation: gemini api outputs strict json with schema versioning + validation + repair prompt if invalid.

srs: sm-2 simplified + immediate error-bag reinforcement during sessions.

logging: never log pdf content or user documents.

di: manual dependency injection only. do NOT use Hilt, Dagger, or Koin. use factories and constructor injection.
