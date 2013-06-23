package net.craftminecraft.bungee.bungeeban.banstore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.Model;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import com.jolbox.bonecp.BoneCPDataSource;

import net.craftminecraft.bungee.bungeeban.util.MainConfig;

public class MySQLBanStore implements IBanStore {

	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
	private Logger logger;
	private BoneCPDataSource bonecp;

	public MySQLBanStore(Logger logger, MainConfig config) {
		this.logger = logger;
		BoneCPConfig bonecpconfig = new BoneCPConfig();
		bonecpconfig.setJdbcUrl("jdbc:mysql://" + config.database_address + ":" + config.database_port + "/" + config.database_name);
		bonecpconfig.setUsername(config.database_username);
		bonecpconfig.setPassword(config.database_password);
		bonecp = new BoneCPDataSource(bonecpconfig);
		Base.open(bonecp);

		Connection connection = Base.connection();
		try {
			if (!(isTable("bungeeban_playerbans", connection))) {
				Statement statement = connection.createStatement();
				statement.execute("CREATE TABLE bungeeban_playerbans (id INT PRIMARY KEY AUTO_INCREMENT, banned VARCHAR(16),"
					+ "source VARCHAR(50), created VARCHAR(50),"
					+ "expiry VARCHAR(50), reason VARCHAR(150),"
					+ "server VARCHAR(50))");
			}
			if (!(isTable("bungeeban_ipbans", connection))) {
				Statement statement = connection.createStatement();
				statement.execute("CREATE TABLE bungeeban_ipbans (id INT PRIMARY KEY AUTO_INCREMENT, banned VARCHAR(16),"
					+ "source VARCHAR(50), created VARCHAR(50),"
					+ "expiry VARCHAR(50), reason VARCHAR(150),"
					+ "server VARCHAR(50))");

			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Failed to create database table. No bans will be stored", e);
			return;
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
			}
		}
	}

	@Override
	public boolean ban(BanEntry entry) {
		if (!Base.hasConnection()) {
			Base.open(bonecp);
		}
		if (entry.isIPBan()) {
			SqlIPBanEntry sqlentry = SqlIPBanEntry.fromBanEntry(entry);
			return sqlentry.saveIt();
		} else {
			SqlPlayerBanEntry sqlentry = SqlPlayerBanEntry.fromBanEntry(entry);
			return sqlentry.saveIt();
		}
	}

	@Override
	public boolean unban(String player, String server) {
		if (!Base.hasConnection()) {
			Base.open(bonecp);
		}
		return SqlPlayerBanEntry.delete("banned = ? AND server = ?", player, server) > 0;
	}

	@Override
	public boolean gunban(String player) {
		if (!Base.hasConnection()) {
			Base.open(bonecp);
		}
		return SqlPlayerBanEntry.delete("banned = ? AND server = '(GLOBAL)'", player) > 0;
	}

	@Override
	public boolean unbanIP(String ip, String server) {
		if (!Base.hasConnection()) {
			Base.open(bonecp);
		}
		return SqlIPBanEntry.delete("banned = ? AND server = ?", ip, server) > 0;
	}

	@Override
	public boolean gunbanIP(String ip) {
		if (!Base.hasConnection()) {
			Base.open(bonecp);
		}
		return SqlIPBanEntry.delete("banned = ? AND server = '(GLOBAL')", ip) > 0;
	}

	@Override
<<<<<<< HEAD
	public Table<String,String,BanEntry> getBanList() {
		removeExpired();
		try {
			ResultSet rs = connection.query("SELECT * FROM bungeeban_playerbans");
			Table<String,String,BanEntry> entries = HashBasedTable.create();
			if (rs.first()) {
				do {
					try {
						BanEntry.Builder builder = new BanEntry.Builder(rs.getString("banned"))
							.created(dateFormat.parse(rs.getString("created")))
							.server(rs.getString("server"))
							.reason(rs.getString("reason"))
							.source(rs.getString("source"));
						if (rs.getNString("expiry") != null) {
							builder.expiry(dateFormat.parse(rs.getNString("expiry")));
						}
						BanEntry entry = builder.build();
						entries.put(entry.getBanned(),entry.getServer(),entry);
					} catch (ParseException e) {
						logger.severe("Invalid date format for entry " + rs.getString("banned") +
							":" + rs.getString("server"));
						continue;
					}
				} while (rs.next());
			}
			return entries;
		} catch (SQLException e) {
			logger.severe("getBanList failed, " + e.getMessage());
			return null;
=======
	public Table<String, String, BanEntry> getBanList() {
		if (!Base.hasConnection()) {
			Base.open(bonecp);
		}
		Table<String, String, BanEntry> returnval = HashBasedTable.create();
		List<SqlPlayerBanEntry> entries = SqlPlayerBanEntry.findAll();
		for (SqlPlayerBanEntry entry : entries) {
			BanEntry beentry = entry.toBanEntry();
			returnval.put(beentry.getBanned(), beentry.getServer(), beentry);
>>>>>>> a20ebe62b287721bf8c42889c3b3cfa994b38de3
		}
		return returnval;
	}

	@Override
<<<<<<< HEAD
	public Table<String,String,BanEntry> getIPBanList() {
		removeExpired();
		try {
			ResultSet rs = connection.query("SELECT * FROM bungeeban_ipbans");
			Table<String,String,BanEntry> entries = HashBasedTable.create();
			if (rs.first()) {
				do {
					try {
						BanEntry.Builder builder = new BanEntry.Builder(rs.getString("banned"))
							.created(dateFormat.parse(rs.getString("created")))
							.server(rs.getString("server"))
							.reason(rs.getString("reason"))
							.source(rs.getString("source"));
						if (rs.getNString("expiry") != null) {
							builder.expiry(dateFormat.parse(rs.getNString("expiry")));
						}
						BanEntry entry = builder.ipban().build();
						entries.put(entry.getBanned(),entry.getServer(),entry);
					} catch (ParseException e) {
						logger.severe("Invalid date format for entry " + rs.getString("banned") +
							":" + rs.getString("server"));
						continue;
					}
				} while (rs.next());
			}
			return entries;
		} catch (SQLException e) {
			logger.severe("getBanList failed, " + e.getMessage());
			return null;
=======
	public Table<String, String, BanEntry> getIPBanList() {
		if (!Base.hasConnection()) {
			Base.open(bonecp);
>>>>>>> a20ebe62b287721bf8c42889c3b3cfa994b38de3
		}
		Table<String, String, BanEntry> returnval = HashBasedTable.create();
		List<SqlIPBanEntry> entries = SqlIPBanEntry.findAll();
		for (SqlIPBanEntry entry : entries) {
			BanEntry beentry = entry.toBanEntry();
			returnval.put(beentry.getBanned(), beentry.getServer(), beentry);
		}
		return returnval;
	}

	@Override
	public BanEntry isBanned(String player, String server) {
<<<<<<< HEAD
		removeExpired();
		PreparedStatement stmt;
		try {
			stmt = connection.prepare("SELECT * FROM bungeeban_playerbans WHERE banned = ? " +
				    "AND server = ?");
			stmt.setString(1, player);
			stmt.setString(2, server);
			ResultSet rs = connection.query(stmt);
			
			if (rs.first()) {
					try {
						BanEntry.Builder builder = new BanEntry.Builder(rs.getString("banned"))
							.created(dateFormat.parse(rs.getString("created")))
							.server(rs.getString("server"))
							.reason(rs.getString("reason"))
							.source(rs.getString("source"));
						if (rs.getNString("expiry") != null) {
							builder.expiry(dateFormat.parse(rs.getNString("expiry")));
						}
						return builder.build();
					} catch (ParseException e) {
						logger.severe("Invalid date format for entry " + rs.getString("banned") +
							":" + rs.getString("server"));
						return null;
					}
			}
		} catch (SQLException e) {
			logger.severe("isBanned failed, " + e.getMessage());
=======
		if (!Base.hasConnection()) {
			Base.open(bonecp);
		}
		List<SqlPlayerBanEntry> entries = SqlPlayerBanEntry.where("banned = ? AND server = ?", player, server);
		if (entries.size() < 1) {
>>>>>>> a20ebe62b287721bf8c42889c3b3cfa994b38de3
			return null;
		}
		return entries.get(0).toBanEntry();
	}

	@Override
	public BanEntry isIPBanned(String ip, String server) {
<<<<<<< HEAD
		removeExpired();
		PreparedStatement stmt;
		try {
			stmt = connection.prepare("SELECT * FROM bungeeban_ipbans WHERE player = ? AND server = ?");
			stmt.setString(1, ip);
			stmt.setString(2, server);
			ResultSet rs = connection.query(stmt);
			
			if (rs.first()) {
					try {
						BanEntry.Builder builder = new BanEntry.Builder(rs.getString("banned"))
							.created(dateFormat.parse(rs.getString("created")))
							.server(rs.getString("server"))
							.reason(rs.getString("reason"))
							.source(rs.getString("source"));
						if (rs.getNString("expiry") != null) {
							builder.expiry(dateFormat.parse(rs.getNString("expiry")));
						}
						return builder.ipban().build();
					} catch (ParseException e) {
						logger.severe("Invalid date format for entry " + rs.getString("banned") +
							":" + rs.getString("server"));
						return null;
					}
			}
			
			return null;
		} catch (SQLException e) {
			logger.severe("Global UnbanIP failed, " + e.getMessage());
=======
		if (!Base.hasConnection()) {
			Base.open(bonecp);
		}
		List<SqlIPBanEntry> entries = SqlIPBanEntry.where("banned = ? AND server = ?", ip, server);
		if (entries.size() < 1) {
>>>>>>> a20ebe62b287721bf8c42889c3b3cfa994b38de3
			return null;
		}
		return entries.get(0).toBanEntry();
	}

	@Override
	public void reloadBanList() {
		return;
	}
	
	private void removeExpired()
	{
		try
		{
			PreparedStatement stmt = connection.prepare("DELETE FROM bungeeban_playerbans WHERE expiry < NOW( )");
			connection.query(stmt);
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try
		{
			PreparedStatement stmt = connection.prepare("DELETE FROM bungeeban_ipbans WHERE expiry < NOW( )");
			connection.query(stmt);
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean isTable(String table, Connection connection) {
		Statement statement;
		try {
			statement = connection.createStatement();
		} catch (SQLException e) {
			this.logger.log(Level.SEVERE, "Could not create a statement in checkTable(), SQLException.", e);
			return false;
		}
		try {
			statement.executeQuery("SELECT * FROM " + table);
			return true; // Result can never be null, bad logic from earlier versions.
		} catch (SQLException e) {
			return false; // Query failed, table does not exist.
		}
	}

	public abstract static class SqlBanEntry extends Model implements BanEntry {

		@Override
		public String getBanned() {
			return this.getString("banned");
		}

		@Override
		public Date getCreated() {
			if (this.getString("created") == null) {
				return null;
			}
			try {
				return MySQLBanStore.dateFormat.parse(this.getString("created"));
			} catch (ParseException e) {
				return null;
			}
		}

		@Override
		public String getSource() {
			return this.getString("source");
		}

		@Override
		public Date getExpiry() {
			if (this.getString("expiry") == null) {
				return null;
			}
			try {
				return MySQLBanStore.dateFormat.parse(this.getString("expiry"));
			} catch (ParseException ex) {
				return null;
			}
		}

		@Override
		public boolean hasExpired() {
			return this.getExpiry() == null ? false : new Date().after(this.getExpiry());
		}

		@Override
		public boolean isTempBan() {
			return this.get("expiry") == null;
		}

		@Override
		public String getReason() {
			return this.getString("reason");
		}

		@Override
		public String getServer() {
			return this.getString("server");
		}

		@Override
		public boolean isGlobal() {
			return this.getServer().equalsIgnoreCase("(GLOBAL)");
		}

		// Returns an (immutable) banentry.
		public abstract BanEntry toBanEntry();
	}

	@org.javalite.activejdbc.annotations.Table("bungeeban_playerbans")
	public static class SqlPlayerBanEntry extends SqlBanEntry {

		@Override
		public boolean isIPBan() {
			return false;
		}

		@Override
		public BanEntry toBanEntry() {
			return new SimpleBanEntry.Builder()
				.banned(this.getBanned())
				.server(this.getServer())
				.created(this.getCreated())
				.expiry(this.getExpiry())
				.reason(this.getReason())
				.source(this.getSource())
				.ipban()
				.build();
		}

		public static SqlPlayerBanEntry fromBanEntry(BanEntry entry) {
			if (entry == null) {
				return null;
			}
			Preconditions.checkArgument(!entry.isIPBan(), "Entry should represent a playerban");
			SqlPlayerBanEntry sqlentry = new SqlPlayerBanEntry();
			sqlentry.setString("banned", entry.getBanned());
			sqlentry.setString("server", entry.getServer());
			sqlentry.setString("created", MySQLBanStore.dateFormat.format(entry.getCreated()));
			sqlentry.setString("expiry", MySQLBanStore.dateFormat.format(entry.getCreated()));
			sqlentry.setString("reason", entry.getReason());
			sqlentry.setString("source", entry.getSource());
			return sqlentry;
		}
	}

	@org.javalite.activejdbc.annotations.Table("bungeeban_ipbans")
	public static class SqlIPBanEntry extends SqlPlayerBanEntry {

		@Override
		public boolean isIPBan() {
			return true;
		}

		@Override
		public BanEntry toBanEntry() {
			return new SimpleBanEntry.Builder()
				.banned(this.getBanned())
				.server(this.getServer())
				.created(this.getCreated())
				.expiry(this.getExpiry())
				.reason(this.getReason())
				.source(this.getSource())
				.ipban()
				.build();
		}

		public static SqlIPBanEntry fromBanEntry(BanEntry entry) {
			if (entry == null) {
				return null;
			}
			Preconditions.checkArgument(entry.isIPBan(), "Entry should represent an IPBan");
			SqlIPBanEntry sqlentry = new SqlIPBanEntry();
			sqlentry.setString("banned", entry.getBanned());
			sqlentry.setString("server", entry.getServer());
			sqlentry.setString("created", MySQLBanStore.dateFormat.format(entry.getCreated()));
			sqlentry.setString("expiry", MySQLBanStore.dateFormat.format(entry.getCreated()));
			sqlentry.setString("reason", entry.getReason());
			sqlentry.setString("source", entry.getSource());
			return sqlentry;
		}
	}
}
