package chapter6.controller;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import chapter6.beans.Message;
import chapter6.logging.InitApplication;
import chapter6.service.MessageService;

@WebServlet(urlPatterns = { "/edit" })
public class EditServlet extends HttpServlet {

	/**
	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");

	/**
	* デフォルトコンストラクタ
	* アプリケーションの初期化を実施する。
	*/
	public EditServlet() {
		InitApplication application = InitApplication.getInstance();
		application.init();
	}

	//つぶやきの編集画面を表示させる
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		Connection connection = null;
		//パラメータのつぶやきIdが数字以外のときとつぶやきIDが削除されているときにエラーを表示する
		String messageid = request.getParameter("messageId");

		if (!messageid.matches("^[1-9]\\d*$") || messageid == null) {
			HttpSession session = request.getSession();
			session.setAttribute("errorMessages", "不正なパラメータが入力されました");
			response.sendRedirect("./");
			return;
		}

		int messageId = Integer.parseInt(request.getParameter("messageId"));
		//DBで検索したつぶやきを取得
		Message editmessage = new MessageService().select(connection, messageId);

		//URLのパラメータが存在しないつぶやきIDになっていたらエラーを表示する
		if (editmessage == null) {
			HttpSession session = request.getSession();
			session.setAttribute("errorMessages", "不正なパラメータが入力されました");
			response.sendRedirect("./");
			return;
		}
		//requestに値をset
		request.setAttribute("message", editmessage);
		//編集画面へfoward
		request.getRequestDispatcher("edit.jsp").forward(request, response);
	}

	//つぶやきの編集をする
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		HttpSession session = request.getSession();
		List<String> errorMessages = new ArrayList<String>();

		int messageId = Integer.parseInt(request.getParameter("messageId"));
		String text = request.getParameter("text");
		//エラーチェック→エラーがなかったらupdate
		if (!isValid(text, errorMessages)) {
			session.setAttribute("errorMessages", errorMessages);
			return;
		}
		Connection connection = null;
		Message updatemessage = new Message();

		//画面で入力されたつぶやきをセット
		updatemessage.setId(messageId);
		updatemessage.setText("text");

		new MessageService().update(connection, messageId, text);
		response.sendRedirect("./");
	}

	//つぶやきのチェック(エラーがある場合、False)
	private boolean isValid(String text, List<String> errorMessages) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		if (StringUtils.isBlank(text)) {
			errorMessages.add("メッセージを入力してください");
		} else if (140 < text.length()) {
			errorMessages.add("140文字以下で入力してください");
		}

		if (errorMessages.size() != 0) {
			return false;
		}
		return true;
	}
}
