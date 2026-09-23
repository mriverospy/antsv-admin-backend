--
-- PostgreSQL database dump
--

-- Dumped from database version 16.6
-- Dumped by pg_dump version 16.6

-- Started on 2025-08-13 15:45:28

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 228 (class 1259 OID 16748)
-- Name: pais; Type: TABLE; Schema: public; Owner: mamarilla
--

CREATE TABLE public.pais (
                             id_pais integer NOT NULL,
                             descripcion character varying(200),
                             gentilicio character varying(100)
);

--
-- TOC entry 229 (class 1259 OID 16751)
-- Name: pais_id_pais_seq; Type: SEQUENCE; Schema: public; Owner: mamarilla
--

CREATE SEQUENCE public.pais_id_pais_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- TOC entry 5140 (class 0 OID 0)
-- Dependencies: 229
-- Name: pais_id_pais_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: mamarilla
--

ALTER SEQUENCE public.pais_id_pais_seq OWNED BY public.pais.id_pais;


ALTER TABLE ONLY public.pais ALTER COLUMN id_pais SET DEFAULT nextval('public.pais_id_pais_seq'::regclass);


--
-- TOC entry 5133 (class 0 OID 16748)
-- Dependencies: 228
-- Data for Name: pais; Type: TABLE DATA; Schema: public; Owner: mamarilla
--

COPY public.pais (id_pais, descripcion, gentilicio) FROM stdin;
2	ARGENTINA	ARGENTINA
1	Paraguay	Paraguaya
3	Afganistán	Afgano
4	Alemania	Alemán
5	Arabia Saudita	Árabe
6	Argentina	Argentino
7	Australia	Australiano
8	Bélgica	Belga
9	Bolivia	Boliviano
10	Brasil	Brasileño
11	Camboya	Camboyano
12	Canadá	Canadiense
13	Chile	Chileno
14	China	Chino
15	Colombia	Colombiano
16	Corea	Coreano
17	Costa Rica	Costarricense
18	Cuba	Cubano
19	Dinamarca	Danés
20	Ecuador	Ecuatoriano
21	Egipto	Egipcio
22	El Salvador	Salvadoreño
23	Escocia	Escocés
24	España	Español
25	Estados Unidos	Estadounidense
26	Estonia	Estonio
27	Etiopia	Etiope
28	Filipinas	Filipino
29	Finlandia	Finlandés
30	Francia	Francés
31	Gales	Galés
32	Grecia	Griego
33	Guatemala	Guatemalteco
34	Holanda	Holandés
35	Honduras	Hondureño
36	Indonesia	Indonés
37	Inglaterra	Inglés
38	Irak	Iraquí
39	Irán	Iraní
40	Irlanda	Irlandés
41	Israel	Israelí
42	Italia	Italiano
43	Japón	Japonés
44	Jordania	Jordano
45	Laos	Laosiano
46	Letonia	Letón
47	Lituania	Letonés
48	Malasia	Malayo
49	Marruecos	Marroquí
50	México	Mexicano
51	Nicaragua	Nicaragüense
52	Noruega	Noruego
53	Nueva Zelanda	Neozelandés
54	Panamá	Panameño
55	Perú	Peruano
56	Polonia	Polaco
57	Portugal	Portugués
58	Puerto Rico	Puertorriqueño
59	Republica Dominicana	Dominicano
60	Rumania	Rumano
61	Rusia	Ruso
62	Suecia	Sueco
63	Suiza	Suizo
64	Tailandia	Tailandés
65	Taiwán	Taiwanes
66	Turquía	Turco
67	Ucrania	Ucraniano
68	Uruguay	Uruguayo
69	Venezuela	Venezolano
70	Vietnam	Vietnamita
\.


SELECT pg_catalog.setval('public.pais_id_pais_seq', 70, true);


ALTER TABLE ONLY public.pais
    ADD CONSTRAINT pk_pais PRIMARY KEY (id_pais);
