package com.tbx.base.model


/**
 * @author Created by TrinhBX.
 * Mail: trinhbx196@gmail.com
 * Phone: +08 988324622
 * @since Date: 11/8/23
 **/

data class ApiRepoList<T>(var data:List<T>)
data class ApiRepoLists<T>(var datas:List<T>)
data class ApiRepoObject<T>(var data:T)
data class ApiRepoObjects<T>(var datas:T)