package com.yandex.travelmap.util

import org.springframework.stereotype.Service

@Service
class MailBuilder {
    fun buildEmail(link: String): String {
        return """<div style="font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c">

<span style="display:none;font-size:1px;color:#fff;max-height:0"></span>

  <table role="presentation" class="m_-6186904992287805515content" align="center" cellpadding="0" cellspacing="0" border="0" style="border-collapse:collapse;max-width:580px;width:100%!important" width="100%">
    <tbody><tr>
      <td height="30"><br></td>
    </tr>
    <tr>
      <td width="10" valign="middle"><br></td>
      <td style="font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px">
<a href="$link">Activate Now</a>
      </td>
      <td width="10" valign="middle"><br></td>
    </tr>
    <tr>
      <td height="30"><br></td>
    </tr>
  </tbody></table><div class="yj6qo"></div><div class="adL">

</div></div>"""
    }
}