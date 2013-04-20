/**
 * 功能及使用说明：
 * $('#form_id').nform(options);根据options的不同，来调用本插件的各个功能
 * 1.初始化：$('#form_id').nform(options);
 *   options要么不填，要么为一json对象，见$.fn.nform.defaults对象
 * 2.清空表单：$('#form_id').nform('reset');
 * 3.提交表单：$('#profileform').nform('submit');
 * 4.清空提示信息：$('#profileform').nform('clear');
 * 5.填充表单：$('#profileform').nform('fill',param);
 */
(function($) {
    var iframe_transport_name = '_nform_transport_iframe_';
    
    /**
     * 功能：清空提示信息
     * 参数：form
     */
    function clear(fm) {
        $(fm).find(".field_error").remove(); //删除出错field后面的错误消息
        $(fm).find('.action_error').remove(); //删除actionerror
        $(fm).find(".error").removeClass('error'); //删除出错field的error样式
    }

    /**
     * json字符串转化为json对象
     */
    function parseJSON(str) {
        if (str.indexOf('(') == 0 && str.indexOf(')', str.length - 1) !== -1) {
            str = str.substring(1, str.length - 1);
        }
        try {
            return $.parseJSON(str);
        }catch(e) {
            return null;
        }
    }
    /**
     * 提交普通数据
     */
    function submitAjax(fm, params) {
        $.post(fm.action, params, function(resp) {
        	$(fm).find(':button').each(function(btn) {
                btn.disabled = false;
            });
            var data = resp;
            if (typeof resp == 'string') {
                data = parseJSON(resp);
            }
            processResponse(fm, data, resp);
        });
    }

    /**
     * 提交上传文件
     */
    function submitIframe(fm) {
        var onload = function(){
            var txt = this.contentWindow.document.body.innerHTML;
            if (txt != null && txt.length > 0) {
                processResponse(fm, parseJSON(txt), txt);
            }
            this.src = 'about:blank';
            $('#' + iframe_transport_name).unbind('load', onload);
        };
        
        $('#' + iframe_transport_name).bind('load', onload);
        fm.submit();
    }

    /**
     * 对提交form后返回的数据进行处理
     */
    function processResponse(fm, data, txt) {
        var options = $(fm).data('nform').options;
        if (data == null && options.onLoad != null ) { //如果服务器返回的不是json数据 并且options中onLoad函数
            options.onLoad.apply(fm, [txt]);
            return;
        }else if (data == null) {
            return;
        }
     	clear(fm);
        if (data.result == 'ok') {
            options.onSuccess.apply(fm, [data.contents]);//掉用提交form成功后的方法
        }
        if (data.result == 'error') {
            options.onError.apply(fm, [data.contents]);//失败后，调用默认的报错方法
            if (options.afterError){ //失败后，调用自定义的报错方法
                options.afterError.apply(fm, [data.contents]);
            }
        }
    }

    /**
     * 功能:提交失败后 默认执行的方法,可以在初始化时通过options参数自定义（一般就使用默认）
     * 参数：
     * 	contents：失败后返回的数据
     */
    function onError(contents) {
        var state = $.data(this, 'nform');
        clear(this);
        var fm = this;
        var errors = contents.errors;
        var fielderrors  = contents.fieldErrors;
        var options = $(this).data('nform').options;
        // var error_table = '<table class="error_table"><tr><td class="error_ico"></td><td class="error_msg"></td></tr></table>';
        //将错误信息显示在出错的field后面
        $.each(fielderrors, function(error_field, error_msgs){
            var msgs = error_msgs.join(',');
            $(fm).find(":input").each(function(){
                if (this.name == error_field) {
                    $('<span class="field_error">' + msgs + '</span>').insertAfter(this);
                    $(this).addClass('error');
                }
            });
        });
        if (errors.length > 0) {
            var msgbar = $('<div class="message"></div>');
            $.each(errors, function(){
                $(msgbar).append('<p>' + this + '</p>').text(this);
            });
            msgbar.insertBefore($(this).children()[0]);
        }
    }
    
    /**
     * 功能:通过options参数来调用各种功能
     * 参数：
     * 	options：json对象或者字符串
     * 	params：json对象，只有options='fill'时有效
     * 返回值：
     * 	返回nform对象
     */
    $.fn.nform = function(method) {
        var methods = {
            init : function(options) {
                var state = $.data(this, 'nform');
                if (state) {
                    if (options) {
                        state.options = $.extend(state.options, options);
                    }
                    return;
                }
                options = options || {};
                if ($(this).parents('.ndialog').length == 1) {
                    options.dialog = $(this).parents('.ndialog')[0];
                }
                state = {//options存放nform的一些初始化参数
                    options: $.extend({}, $.fn.nform.defaults, options),
                    form: this
                };
                $(this).attr('onsubmit', 'return false');//清空form中提交事件
                if ($(this).attr('enctype') == 'multipart/form-data' && $(this).find(":file").length > 0) {
                    state.options.transport = 'iframe';
                }
                if(state.options.transport == 'iframe' && $('#' + iframe_transport_name).length == 0) {
                    //创建iframe元素，附加到body上，用以上传文件
                    $('<iframe></iframe>', {
                        src     : 'about:blank',
                        enctype : 'multipart/form-data',
                        name    : iframe_transport_name,
                        id      : iframe_transport_name
                    }).css('position', 'absolute').css('top', '-9999px').appendTo(document.body);
                    $(this).attr('target', iframe_transport_name);
                }
                $.data(this, 'nform', state);
                
                return this;
            },
            submit : function() {
                clear(this);
                var params = $(this).serialize();//把form表单的值序列化（name=123&passwd=456）
                /*
                 * 遍历form中的checkbox，如果checkbox没有被选中，
                 * 但它有个属性unchecked_value不为空的话，需要提交这个checkbox，
                 * 且value等于unchecked_value属性的值
                 */
                $(this).find(':checkbox').each(function(cbx) {
                    if(!this.checked && $(this).attr('unchecked_value') != null){
                        params += '&' + this.name + '=' + $(this).attr('unchecked_value');
                    }
                });
                $(this).find(':button').each(function(btn) {
                    btn.disabled = true;
                });
                var options = $.data(this, 'nform').options;
                if (options.transport == 'ajax') {
                    submitAjax(this, params);
                }else if (options.transport == 'iframe') {
                    submitIframe(this, params);
                }else{
                    alert('This transport is not supported');
                }
            },
            reset : function() {
                clear(this);
                this.reset();
                var options = $(this).data('nform').options;
                if (options.onReset) {
                    options.onReset.apply(this);
                }
                return this;
            },
            fill : function(param, param_ext, onSuccess) {
                if (typeof param == 'object') {
                    var paramstr = decodeURIComponent($.param(param, true));//把param对象 转化为类似'name=123&password=456'的字符串
                    var valuemap = {};
                    $(paramstr.split('&')).each(function(i, pair){
                        var keyvalue = pair.split('=');
                        var k = keyvalue[0];
                        var v = keyvalue[1];
                        if (!valuemap[k]) {
                            valuemap[k] = v;
                        }else if(!$.isArray(valuemap[k])) {
                            valuemap[k].push([valuemap[k], v]);
                        }else{ // valuemap[k] is array
                            valuemap[k].push(v);
                        }
                    });
                    $this = this;
                    $.each(valuemap, function(k, v){
                        //k = k.replace('[', '\\[').replace(']', '\\]')
                        //turn to Global replacement
                        var k = k.replace(/\[/g, '\\[').replace(/\]/g, '\\]');
                        var els = $($this).find("input[name=" + k  + "]");
                        if (els.length === 0){
                            els = $($this).find("select[name=" + k  + "]")
                        }
                        
                        $.each(els, function(){
                            $(this).val(v);
                        });
                        
                        //$(this).find("input[name=" + k  + "]").each(function(){
                        //    $(this).val(v);
                        //});
                    });
                }else{ //param is url
                    var url = param;
                    $.post(url, param_ext, function(resp) {
                        $(this).nform('fill', parseJSON(resp));
                        if (onSuccess) {
                            onSuccess.apply(this);
                        }
                    });
                }
            }
        };
        if (methods[method]) {
            var args = Array.prototype.slice.call(arguments, 1);
            return this.each(function () {
                return methods[method].apply(this, args);
            });
        } else if (typeof method === 'object' || !method) {
            var args = arguments;
            return this.each(function () {
                return methods.init.apply(this, args);
            });
        } else {
            $.error('Method ' + method + ' does not exist!');
        }
    };
    
    $.fn.nform.defaults = {//nform对象默认参数
        transport : 'ajax',    //'ajax'提交普通数据，'iframe'提交文件上传
        onSuccess : document.location.reload, //表单验证成功后回调函数
        onError   : onError,   //表单验证失败后回调函数
        onLoad    : null,      //服务器返回非json数据时的回调函数
        onReset   : null,      //执行 $(form).nform('reset')时进行的操作
        dialog    : null,      //dialog or cpanel id。提交成功后自动关闭
        afterError: null      //onError执行后，的附加回调函数
    };
})(jQuery);
