# Schema Update Instructions

To create a new migration:

1. Copy the `updates/n.sql.template` file.
2. Rename the copy to the next schema version, e.g., `1.sql`, `2.sql`, etc in the `updates/` folder.
3. Edit the new `.sql` file to include your schema changes.
4. Ensure each `.sql` file is only a **SINGLE** transaction.  
5. Do **not** include `versioning.increment_schema_version` in the SQL - the Java `SchemaUpdater` will handle version increments automatically.
