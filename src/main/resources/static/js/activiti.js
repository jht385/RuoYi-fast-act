// 发起流程
function submitApply(id, name, version) {
	var url = prefix + "/processForm?id=" + id;
	url += '&name=' + name;
	url += '&version=' + version;
    $.modal.open(name, url, window.innerWidth, window.innerHeight);
}

// 查看审批历史
function showHistoryDialog(instanceId) {
  var url = ctx + 'process/historyList/' + instanceId;
  $.modal.open("查看审批历史", url, null, null, null, true);
}

// 查看进度图
function showProcessingImgDialog(instanceId) {
  var url = ctx + 'process/processImg/' + instanceId;
  $.modal.open("查看流程图", url, 1200, null, null, true);
}

// 用于委托用户
function selectUser(taskId) {
  var url = ctx + 'user/authUser/selectUser?taskId=' + taskId;
  $.modal.open("关联系统用户", url);
}

// 处理详情
function handleDetail(actName, curTaskFormKey, procInstId, taskId, businessKey, curTaskDefKey) {
    var url = ctx + curTaskFormKey.substring(1) + "?procInstId=" + procInstId + "&taskId=" + taskId + "&processId=" + businessKey + "&node=" + curTaskDefKey + "&action=handle";
    $.modal.open(actName, url, window.innerWidth, window.innerHeight);
}

// 查看详情
function viewDetail(actName, curTaskFormKey, procInstId, businessKey) {
    var url = ctx + curTaskFormKey.substring(1) + "?procInstId=" + procInstId + "&processId=" + businessKey + "&action=view";
    var btn = ['<i class="fa fa-close"></i> 关闭'];
	var options = {
		title: actName,
		width: window.innerWidth,
		height: window.innerHeight,
		url: url,
		btn: btn,
		callBack: function(index, layero){
			$.modal.close(index);
		}
	};
	$.modal.openOptions(options);
}

// 编辑详情
function editDetail(actName, curTaskFormKey, procInstId, businessKey) {
    var url = ctx + curTaskFormKey.substring(1) + "?procInstId=" + procInstId + "&processId=" + businessKey + "&action=edit";
    var btn = ['<i class="fa fa-check"></i> 确认', '<i class="fa fa-close"></i> 关闭'];
	var options = {
		title: actName,
		width: window.innerWidth,
		height: window.innerHeight,
		url: url,
		btn: btn,
		yes: function(index, layero) {
			var iframeWin = layero.find('iframe')[0];
			iframeWin.contentWindow.submitHandler(index, layero);
			//$.modal.close(index);
		},
		btn2: function(index, layero){
			$.modal.close(index);
		}
	};
	$.modal.openOptions(options);
}

function showFormDialog(instanceId, module) {
  var url = ctx + module + "/showFormDialog/" + instanceId;
  $.modal.open('流程详情', url, null, null, null, true);
}

function makeBLabel(value, color){
	return '<b style="color:'+ color +'">'+ value +'<b/>';
}

//计算时间差，单位秒
function calcTotalSecond(startDateStr, endDateStr) {
  var date1 = new Date(startDateStr);             // 开始时间
  var date2 = new Date(endDateStr);               // 结束时间
  var timeSub = date2.getTime() - date1.getTime();  // 时间差毫秒
  return timeSub / 1000;
}

// 计算出相差天数
function formatTotalDateSub (secondSub) {
  var days = Math.floor(secondSub / (24 * 3600));     // 计算出小时数
  var leave1 = secondSub % (24*3600) ;                // 计算天数后剩余的毫秒数
  var hours = Math.floor(leave1 / 3600);              // 计算相差分钟数
  var leave2 = leave1 % (3600);                       // 计算小时数后剩余的毫秒数
  var minutes = Math.floor(leave2 / 60);              // 计算相差秒数
  var leave3 = leave2 % 60;                           // 计算分钟数后剩余的毫秒数
  var seconds = Math.round(leave3);
  return days + " 天 " + hours + " 时 " + minutes + " 分 " + seconds + ' 秒';
}