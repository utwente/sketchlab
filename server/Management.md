# Server setup

### General
- Server IP: `linux455.utsp.utwente.nl`
- Management user: `sketchlab` (please don't use root too much)

### Services
Two services, `sketchlab-prod` and `sketchlab-test`, running on AJP port `8209` and `8009` respectively. PostgreSQL runs to support production.
Both run under the `sketchlab` user and are managed by `systemd` as user services.

#### Sketchlab-prod
Uses postgres, persistent database, real utwente authentication. Gets data provisioned via `INSERT` and `UPDATE`-s executed by external UT managed provisioning service.

#### Sketchlab-test
Uses an embedded in-memory database (h2db). Every time you restart it, the database is cleared and re-initialized with test data.

### Binaries
All sketchlab binaries are owned by the `sketchlab` user and live in `/home/sketchlab/binaries`.

All systemd unit files are stored in there as well (i.e. `sketchlab-test.service` lives in `/home/sketchlab/binaries/sketchlab-test/sketchlab-test.service`).

### Management commands
Systemd unit files are deployed as "user" services, meaning you only need access to the sketchlab user to manage them.
The services are `sketchlab-prod` and `sketchlab-test`.

Some commands, using `sketchlab-test` as an example:

#### `sketchctl`
Sketchlab can be managed directly with systemd interaction, but for ease of use a management script exists: `sketchctl` (also in this directory).
This script is registered on the `PATH` set in `/etc/profile.d/`. It is located in `/home/sketchlab/scripts`.

**Run these under user `sketchlab`**. The script will complain and not work if you run it as another user.

- Help: `sketchctl`
- Status: `sketchctl test`
- Starting: `sketchctl test start`
- Stopping: `sketchctl test stop`
- View output (console): `sketchctl test log`
- Upgrading: `sketchctl test deploy <path to new JAR>`. This will stop the instance `test`, put the current jar in
`~/binaries/sketchlab-test/archive`, copy the JAR and start the instance again. 

Replace `test` with `prod` for production management.

### PostgreSQL
#### Service
- Sytemd: `postgresql` (as system service)
- Port: `5432` (default, on all interfaces, not opened in firewall)
- Password login is enabled and a root password is set.

#### Management
Currently PostgreSQL only accepts incoming connections from `localhost`, and even if it didn't port `5432` is blocked in the firewall.
In order to login with external tools, you can use SSH port forwarding, i.e.

`ssh -L 5432:localhost:5432 sketchlab@linux455.utsp.utwente.nl`

Now you can connect with any tool to `localhost:5432` on your own machine. This also allows running sketchlab locally in production mode with identical
configuration as on the server.

The Sketchlab schema needs the `pgcrypto` extension, which can only be set by a superuser with `CREATE EXTENSION IF NOT EXISTS pgcrypto;`.

#### Credentials
- Management: username: `postgres`, password: `*****`.
- Sketchlab: username: `sketchlab`, password: `*****` (owns `sketchlab` db)
- User provisioning: username: `sketchlab_user_provisioning`, password: `*****` (can log in remotely and has access to the `utwente_user_view`).