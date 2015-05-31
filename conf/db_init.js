// MongoDB initialization script

// Unique indexes
db.tech.ensureIndex( { name: 1 }, { unique: true } )
db.comp.ensureIndex( { name: 1 }, { unique: true } )
db.comp.ensureIndex( { website: 1 }, { unique: true } )
db.user.ensureIndex( { email: 1 }, { unique: true } )
db.city.ensureIndex( { handle: 1 }, { unique: true } )