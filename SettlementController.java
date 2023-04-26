package jp.co.internous.peppermill.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import jp.co.internous.peppermill.model.domain.MstDestination;
import jp.co.internous.peppermill.model.mapper.MstDestinationMapper;
import jp.co.internous.peppermill.model.mapper.TblCartMapper;
import jp.co.internous.peppermill.model.mapper.TblPurchaseHistoryMapper;
import jp.co.internous.peppermill.model.session.LoginSession;

/**
 * 決済に関する処理を行うコントローラー
 * @author インターノウス
 *
 */
@Controller
@RequestMapping("/peppermill/settlement")
public class SettlementController {

	@Autowired
	private TblCartMapper cartMapper;

	@Autowired
	private MstDestinationMapper destinationMapper;

	@Autowired
	private TblPurchaseHistoryMapper historyMapper;

	@Autowired
	private LoginSession loginSession;

	private Gson gson = new Gson();

	/**
	 * 宛先選択・決済画面を初期表示する。
	 * @param m 画面表示用オブジェクト
	 * @return 宛先選択・決済画面
	 */
	@RequestMapping("/")
	public String index(Model m) {

		int userId = loginSession.getUserId();

		List<MstDestination> destinationId = destinationMapper.findByUserId(userId);

		m.addAttribute("loginSession", loginSession);

		m.addAttribute("destinations", destinationId);

		return "settlement";
	}

	/**
	 * 決済処理を行う
	 * @param destinationId 宛先情報id
	 * @return true:決済処理成功、false:決済処理失敗
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/complete")
	@ResponseBody
	public boolean complete(@RequestBody String destinationId) {

		Map<String, String> map = gson.fromJson(destinationId, Map.class);
		int id = Integer.parseInt(map.get("destinationId"));

		int userId = loginSession.getUserId();

		int insertCount = historyMapper.insert(id, userId);

		int deleteCount = 0;
		if (insertCount > 0) {
			deleteCount = cartMapper.deleteByUserId(userId);
		}

		return deleteCount == insertCount;
	}
}
