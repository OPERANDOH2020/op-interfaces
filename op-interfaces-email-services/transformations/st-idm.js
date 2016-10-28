/**
 * Created by salboaie on 2/15/16.
 * IDM: IDentity Management swarm
 */

module.exports.st_idm = {
    name: "Identities Manager",
    generateIdentity:{
        method : 'put',
        params: ["userId"],
        path   : '/addIdentity/$userId/',
        code:function(){

        }
    },
    removeIdentity:{
        method : 'delete',
        params: ["userId", "identity"],
        path   : '/removeIdentity/$userId/$identity',
        code:function(){

        }
    },
    listIdentities:{
        method : 'get',
        params: ["userId"],
        path   : '/listIdentities/$userId/',
        code:function(){

        }
    }
}
