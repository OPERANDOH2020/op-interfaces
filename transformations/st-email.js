/**
 * Created by salboaie on 2/15/16.
 * email services
 */

module.exports.st-email = {
    name:"SendEmail interface",
    sendEmail:{
        method : 'put',
        params: ["userId", "__to","__cc", "__bcc","__subject", "__content"],
        path   : '/sendEmail/$userId/',
        code:function(userId, __to,__cc, __bcc,__subject, __content){

        }
    },
    sendEmailWithAttachement:{
        method : 'delete',
            params: ["userId", "__to","__cc", "__bcc","__subject", "__content", "__attachments"],
            path   : '/removeIdentity/$userId/$identity',
            code:function(userId, __to,__cc, __bcc,__subject, __content, __attachments){

        }
    }

}



