var express = require('express'); //Express library
var bodyParser = require('body-parser');
var multer = require('multer'); // v1.0.5
var keywords = require('gramophone');
var mongoose = require('mongoose');
var ddg = require('ddg');

var upload = multer(); // for parsing multipart/form-data
var app = express();

/*-CONFIGURE MONGODB-*/
mongoose.connect('mongodb://localhost/note', function(err){
 if (err) {
    throw err;
  }
});
/*-----------------*/

/*--SHEMA DE LA TABLE DANS LA BASE DE DONNEES--*/
var noteShema = new mongoose.Schema({
  id  : String,
  content : String,
  author : String,
  creationDate  : { type : Date, default : Date.now },
  title : String,
  collaborators : { type : Array , default : [] },
  keywords  : { type : Array , default : [] },
  lastUpdate : { type : Date, default : Date.now },
  smartWord : { type : Array, default : [] },
  smartDef : { type : Array, default : [] }
});
/*--------------------------------------------*/

//MODEL -> RELAIS ENTRE LE TUBLE ET LA BASE DE DONNEES
var noteModel = mongoose.model('notes', noteShema);

/*--AGENT AJOUTER DES MOTS CLEFS--*/
var add_keywords = function(note){
    note.keywords = keywords.extract(note.content);
};
/*--------------------------------*/

/*-----AGENT SMART CONTENT-------*/
var add_smart_content = function(note){

  var matches = [];
  var regex = /\[([^\]]+)\]/;
  matches = regex.exec(note.content);
  var word;

  if(matches.length == 0){
    console.log('O smartContents');
  }
  else
  {
    for(var i=0; i < matches.length; i++)
    {
      if( i % 2 == 1){
        word = matches[i];
        ddg.query(word, function(err, data)
        {
            resulats = data.RelatedTopics;
            if(resulats.length > 0)
            {
              console.log("WORD : " + word);
              console.log("DEFINITION : " + resulats[0].Text);

              note.smartWord.push(word);
              note.smartDef.push(resulats[0].Text);

              console.log(note.smartWord.toString());
              console.log(note.smartDef.toString());

              console.log('AJOUT DU SMART CONTENT');
            }
            else
            {
              console.log('ERREUR keywords');
            }


        });
      }
    }
  }
};

/*------------------------------*/


/*-----------------------------------------------------------------------------------------------------*/
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

console.log("SERVEUR NOTESHARE : STATUT ON");
console.log("===============================\n\n");

/*---Agent environnement---*/
//UPLOAD d'une note sur le serveur
//INSERT dans la base de données
//OK
app.post('/', upload.array(), function(req, res, next){

    //Objet message de retour
    var return_message = {
      code : 0, //-> O == tout est ok
      message : "Note enregistré"
    };

    console.log("RECEPTION D'UNE NOTE :");
    console.log("-----------------------\n\n");
    console.log(req.body);


    //req.body = add_smart_content(req.body);

    var note = new noteModel(req.body); //On ajoute la note dans le model
    add_keywords(note);
    
    //console.log('AJOUT KEYWORDS : ' + note.keywords + '\n')
    //console.log('AJOUT SMART CONTENT :  ' + note.smartWord.toString()+ '\n');


    var query = noteModel.find(null);
    query.where('id', req.body.id);
    query.limit(1);

    query.exec(function(err, comms){
      if(err){
        throw err;
      }
      else{
            //NOUVELLE NOTE
            if(comms.length == 0){
              note.save(function(err){
                if(err){
                  throw err;
                }
                else{
                  noteModel.update({ id : req.body.id }, { smartWord : 'UTC' , smartDef : 'UTC est une très grande école d\'ingénieur en France'}, function(err){
                    if(err){
                      throw err;
                    }
                    else {
                      console.log('NOTE AJOUTÉ AVEC SUCCES\n');
                      console.log("===============================\n");
                      return_message.code = 1;
                      return_message.message = "Note ajouté";
                    }
                  });
                }
              });
            }
            //MODIFIER UNE NOTE
            else{
                  noteModel.update({ id : req.body.id }, { title : req.body.title , content : req.body.content , lastUpdate : req.body.lastUpdate, keywords : req.body.keywords }, function(err){
                    if(err){
                      throw err;
                    }
                    else {
                      console.log('NOTE MODIFIÉ AVEC SUCCES\n');
                      console.log("===============================\n");
                      return_message.code = 2;
                      return_message.message = "Note modifié";
                    }
                });
            }
      }
    });

    res.json(return_message);
});

app.post('/collaborators', upload.array(), function(req, res, next){

  //Objet message de retour
  var return_message = {
    code : 0, //-> O == tout est ok
    message : "Note mis à jour"
  };

  console.log("MODIFICATION COLLABORATEURS");
  console.log("-----------------------\n\n");
  console.log(req.body); //On affiche la note

  noteModel.update({ id : req.body.id }, { collaborators : req.body.collaborators }, function(err){
    if(err){
      console.log('Erreur mis à jour collaborateur\n');
      console.log("===============================\n");
      return_message.code = 1;
      return_message.message = "Erreur maj"
    }
    else {
      console.log('Mis à jour des collaborateur effectués\n');
      console.log("===============================\n");
    }
  });

  res.json(return_message);
});

app.post('/delete', upload.array(), function(req, res){
  console.log('SUPRESSION NOTE');
  console.log('---------------\n\n');
  console.log(req.body);


    noteModel.remove(req.body,function(err){
      if(err){
        console.log('FAIL SUPRESSION NOTES\n');
        console.log('===============================\n');
      }
      else{
        console.log('SUCCESS SUPRESSION NOTES\n');
        console.log('===============================\n');
      }
    });


  res.json(req.body);

});

/*--------*/
app.get('/', function(req, res){
  console.log("TEST");
})

/*--Chercher une note par login--*/
app.get('/login/:login', function(req, res){
    console.log("RECEPTION LOGIN : " + req.params.login);
    console.log("-----------------------\n\n");

    noteModel.find({ $or : [ { author: req.params.login } , { collaborators: req.params.login } ] }, function(err, comms){
          if(err) {
            var return_message = {
              code : 1,
              message : "Erreur note : by login"
            };
            res.json(return_message);
            console.log('Erreur note : login\n');
            console.log("===============================\n");
          }
          else {
            console.log(comms);
            res.json(comms);//Retourner une liste de note
            console.log('Note renvoyé au client : login\n');
            console.log("===============================\n");
          }
    });
});
/*--------------*/

app.get('/login/:login/keywords/:keywords', function(req, res){
  console.log("RECEPTION LOGIN : " + req.params.login + "KEYWORDS : " + req.params.keywords);
  console.log("-----------------------\n\n");

  var tmp = req.params.keywords.replace(","," ");
  var regex = new RegExp('/' + tmp + '/', "g");

  noteModel.find({ author: req.params.login, keywords: {'$regex': tmp }}, function(err, comms){
      if(err){
        var return_message = {
          code : 1,
          message : "Erreur recherche : keywords"
        };
        res.json(return_message);
        consol.log('Erreur recherche : keyords \n');
        console.log("===============================\n");
      }
      else {
        res.json(comms);
        console.log('Note(s) renvoyé au client \n');
        console.log("================================\n");
      }
  })
});
/*--------------------------*/

app.listen(8080);
