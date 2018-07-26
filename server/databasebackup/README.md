# Database Backup Manager Utility
This application creates and manages backups for SQL databases. For now, only PostgreSQL databases are supported.
## Description
Creates and manages backups for the given databases. Will first create a backup for a database. Manages the backups by making sure not too much disk space is used by removing obsolete databases. It does this by using the following scheme:
 - For the past 2 hours: all backups are kept.
 - For the past 3 days: One backup per hour is kept.
 - For the past 90 days: One backup per day is kept.
 - Older than 90 days: Deemed obsolute, so remove everything.
 
 Furthermore, only stores backups as seperate files if there are actual changes in the backup, otherwise, it renames the old backup to the new backup.
## Running the application
The application needs a location in which to store the created backups. This location can be passed on by using the `-b` or `--backup-directory` flags. Only one such flag is supported. To create and manage backups for databases specify these by using the `-d` or `--database` flags, multiple of these flags are supported.
