package chapter6.dao;

import static chapter6.utils.CloseableUtil.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import chapter6.beans.Message;
import chapter6.exception.SQLRuntimeException;
import chapter6.logging.InitApplication;

public class MessageDao {

	/**
	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");

	/**
	* デフォルトコンストラクタ
	* アプリケーションの初期化を実施する。
	*/
	public MessageDao() {
		InitApplication application = InitApplication.getInstance();
		application.init();

	}

	public void insert(Connection connection, Message message) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		PreparedStatement ps = null;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("INSERT INTO messages ( ");
			sql.append("    user_id, ");
			sql.append("    text, ");
			sql.append("    created_date, ");
			sql.append("    updated_date ");
			sql.append(") VALUES ( ");
			sql.append("    ?, "); // user_id
			sql.append("    ?, "); // text
			sql.append("    CURRENT_TIMESTAMP, "); // created_date
			sql.append("    CURRENT_TIMESTAMP "); // updated_date
			sql.append(")");

			ps = connection.prepareStatement(sql.toString());

			ps.setInt(1, message.getUserId());
			ps.setString(2, message.getText());

			ps.executeUpdate();
		} catch (SQLException e) {
			log.log(Level.SEVERE, new Object() {
			}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw new SQLRuntimeException(e);
		} finally {
			close(ps);
		}
	}

	//つぶやきの削除
	public void delete(Connection connection, Message deletemessage) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		PreparedStatement ps = null;
		try {
			StringBuilder sql = new StringBuilder();
			//sqlで削除するためのコマンド
			sql.append("DELETE FROM messages ");
			sql.append("WHERE id = ?");
			//Sqlに接続
			ps = connection.prepareStatement(sql.toString());
			//バインド変数で設定していたメッセージIDに値をセットする
			ps.setInt(1, deletemessage.getId());
			//実行
			ps.executeUpdate();
		} catch (SQLException e) {//例外処理
			log.log(Level.SEVERE, new Object() {
			}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw new SQLRuntimeException(e);
		} finally {
			close(ps);
		}
	}

	//つぶやきの編集のためにDBからつぶやきを検索してedit.jspに返す
	public Message select(Connection connection, int messageId) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		PreparedStatement ps = null;
		try {
			//sqlのコマンドを入力
			String sql = "SELECT * FROM messages WHERE id = ?";
			//sqlとつなぐ
			ps = connection.prepareStatement(sql);
			//値をセット
			ps.setInt(1, messageId);
			//sqlを検索
			ResultSet rs = ps.executeQuery();

			List<Message> editmessage = toMessages(rs);
			if (editmessage.isEmpty()) {
				return null;
			} else if (2 <= editmessage.size()) {
				log.log(Level.SEVERE, "つぶやきが重複しています", new IllegalStateException());
				throw new IllegalStateException("つぶやきが重複しています");
			} else {
				return editmessage.get(0);
			}
		} catch (SQLException e) {
			log.log(Level.SEVERE, new Object() {
			}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw new SQLRuntimeException(e);
		} finally {
			close(ps);
		}
	}

	//つぶやきの行が複数取れる可能性があるのでリストに詰める→1つだけ取れているかselectで確認
	private List<Message> toMessages(ResultSet rs) throws SQLException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		List<Message> editmessages = new ArrayList<Message>();
		try {
			while (rs.next()) {
				Message editmessage = new Message();
				editmessage.setId(rs.getInt("id"));
				editmessage.setUserId(rs.getInt("user_id"));
				editmessage.setText(rs.getString("text"));
				editmessage.setCreatedDate(rs.getTimestamp("created_date"));
				editmessage.setUpdatedDate(rs.getTimestamp("updated_date"));

				editmessages.add(editmessage);
			}
			return editmessages;
		} finally {
			close(rs);
		}
	}

	//つぶやきの編集→DBの更新
	public void update(Connection connection, int messageId, String text) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		PreparedStatement ps = null;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE messages SET ");
			sql.append("    text = ?, ");
			sql.append("    updated_date = CURRENT_TIMESTAMP ");
			sql.append("WHERE id = ?");
			//sqlと接続
			ps = connection.prepareStatement(sql.toString());
			//更新する値をセット
			ps.setString(1, text);
			ps.setInt(2, messageId);
			//DBを更新
			ps.executeUpdate();
		} catch (SQLException e) {
			log.log(Level.SEVERE, new Object() {
			}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw new SQLRuntimeException(e);
		} finally {
			close(ps);
		}
	}

}