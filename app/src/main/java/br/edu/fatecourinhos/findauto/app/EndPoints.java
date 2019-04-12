package br.edu.fatecourinhos.findauto.app;

public class EndPoints {

    // localhost url
    // public static final String BASE_URL = "http://192.168.0.101/gcm_chat/v1";

    public static final String BASE_URL = "http://10.0.2.2:8080/ComercVeiculos";
    public static final String NEW_ADVERTISEMNT = BASE_URL + "/novoAnuncio.php";
    public static final String REGISTER_USER = BASE_URL + "/cadastroUser.php";
    public static final String UPDATE_USER = BASE_URL + "/alterarUser.php";
    public static final String UPLOAD_FOTO = BASE_URL + "/savePhoto.php";
    public static final String LIST_ANUNCIOS = BASE_URL + "/getAnuncios.php";
    public static final String ANUNCIO_COMPLETO = BASE_URL + "/getAnuncioCompleto.php";
    public static final String MEUS_ANUNCIOS = BASE_URL + "/getMeusAnuncios.php";
    public static final String MEUS_ANUNCIOS_FAVORITOS = BASE_URL + "/getMeusAnunciosFavoritos.php";
    public static final String IS_FAVORITE = BASE_URL + "/isFavoriteAnuncio.php";
    public static final String LOGIN = BASE_URL + "/v1/user/login";
    public static final String USER = BASE_URL + "/v1/user/_ID_";
    public static final String CHAT_ROOMS = BASE_URL + "/v1/chat_rooms";
    public static final String CHAT_THREAD = BASE_URL + "/v1/chat_rooms/_ID_";
    public static final String CHAT_ROOM_MESSAGE = BASE_URL + "/v1/chat_rooms/_ID_/message";

    public static final String ROOT_URL = "http://10.0.2.2:8080/ComercVeiculos/chatAppRestApiMaster/v1/";

    public static final String URL_REGISTER = ROOT_URL + "register";
    public static final String URL_STORE_TOKEN = ROOT_URL + "storegcmtoken/";
    public static final String URL_GET_CHAT_ROOMS = ROOT_URL + "getChatRooms";
    public static final String URL_FETCH_MESSAGES = ROOT_URL + "messages";
    public static final String URL_SEND_MESSAGE = ROOT_URL + "send";
}
