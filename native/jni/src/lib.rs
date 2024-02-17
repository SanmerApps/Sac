use std::array;
use std::path::Path;

use jni::objects::{JClass, JFloatArray, JObject, JObjectArray, JString, JValue};
use jni::sys::{jfloat, jint, jlong, jsize};
use jni::{errors, JNIEnv};

use sac::{Endian, Sac, SacHeader};

pub trait JNIEnvExt {
    fn get_float_field(&mut self, obj: &JObject, name: &str) -> errors::Result<jfloat>;
    fn set_float_field(&mut self, obj: &JObject, name: &str, val: jfloat) -> errors::Result<()>;
    fn get_int_field(&mut self, obj: &JObject, name: &str) -> errors::Result<jint>;
    fn set_int_field(&mut self, obj: &JObject, name: &str, val: jint) -> errors::Result<()>;
    fn get_boolean_field(&mut self, obj: &JObject, name: &str) -> errors::Result<bool>;
    fn set_boolean_field(&mut self, obj: &JObject, name: &str, val: bool) -> errors::Result<()>;
    fn get_string_field(&mut self, obj: &JObject, name: &str) -> errors::Result<String>;
    fn set_string_field(&mut self, obj: &JObject, name: &str, val: &String) -> errors::Result<()>;
    fn get_float_array_field<const N: usize>(
        &mut self,
        obj: &JObject,
        name: &str,
    ) -> errors::Result<[jfloat; N]>;
    fn set_float_array_field(
        &mut self,
        obj: &JObject,
        name: &str,
        vals: &[jfloat],
    ) -> errors::Result<()>;
    fn get_string_array_field<const N: usize>(
        &mut self,
        obj: &JObject,
        name: &str,
    ) -> errors::Result<[String; N]>;
    fn set_string_array_field(
        &mut self,
        obj: &JObject,
        name: &str,
        vals: &[String],
    ) -> errors::Result<()>;
}

impl<'local> JNIEnvExt for JNIEnv<'local> {
    #[inline]
    fn get_float_field(&mut self, obj: &JObject, name: &str) -> errors::Result<jfloat> {
        self.get_field(obj, name, "F")?.f()
    }

    #[inline]
    fn set_float_field(&mut self, obj: &JObject, name: &str, val: jfloat) -> errors::Result<()> {
        self.set_field(obj, name, "F", JValue::from(val))
    }

    #[inline]
    fn get_int_field(&mut self, obj: &JObject, name: &str) -> errors::Result<jint> {
        self.get_field(obj, name, "I")?.i()
    }

    #[inline]
    fn set_int_field(&mut self, obj: &JObject, name: &str, val: jint) -> errors::Result<()> {
        self.set_field(obj, name, "I", JValue::from(val))
    }

    #[inline]
    fn get_boolean_field(&mut self, obj: &JObject, name: &str) -> errors::Result<bool> {
        self.get_field(obj, name, "Z")?.z()
    }

    #[inline]
    fn set_boolean_field(&mut self, obj: &JObject, name: &str, val: bool) -> errors::Result<()> {
        self.set_field(obj, name, "Z", JValue::from(val))
    }

    #[inline]
    fn get_string_field(&mut self, obj: &JObject, name: &str) -> errors::Result<String> {
        let owned = self.get_field(obj, name, "Ljava/lang/String;")?;
        let obj = JString::from(owned.l()?);

        let value: String = self.get_string(&obj)?.into();

        Ok(value)
    }

    #[inline]
    fn set_string_field(&mut self, obj: &JObject, name: &str, val: &String) -> errors::Result<()> {
        let value = self.new_string(val)?;

        let value_obj = JObject::from(value);
        self.set_field(obj, name, "Ljava/lang/String;", JValue::from(&value_obj))
    }

    #[inline]
    fn get_float_array_field<const N: usize>(
        &mut self,
        obj: &JObject,
        name: &str,
    ) -> errors::Result<[jfloat; N]> {
        let owned = self.get_field(obj, name, "[F")?;
        let obj = JFloatArray::from(owned.l()?);

        let mut array = [0 as jfloat; N];
        self.get_float_array_region(obj, 0, &mut array)?;

        Ok(array)
    }

    #[inline]
    fn set_float_array_field(
        &mut self,
        obj: &JObject,
        name: &str,
        vals: &[jfloat],
    ) -> errors::Result<()> {
        let float_array = self.new_float_array(vals.len() as jsize)?;
        self.set_float_array_region(&float_array, 0, vals)?;

        let array_obj = JObject::from(float_array);
        self.set_field(obj, name, "[F", JValue::from(&array_obj))
    }

    #[inline]
    fn get_string_array_field<const N: usize>(
        &mut self,
        obj: &JObject,
        name: &str,
    ) -> errors::Result<[String; N]> {
        let owned = self.get_field(obj, name, "[Ljava/lang/String;")?;
        let obj = JObjectArray::from(owned.l()?);

        let mut array: [String; N] = array::from_fn(|_| String::new());
        let length = self.get_array_length(&obj)?.min(N as jsize);

        for index in 0..length {
            let element_obj = self.get_object_array_element(&obj, index)?;
            let element = JString::from(element_obj);
            let value: String = self.get_string(&element)?.into();
            array[index as usize] = value
        }

        Ok(array)
    }

    #[inline]
    fn set_string_array_field(
        &mut self,
        obj: &JObject,
        name: &str,
        vals: &[String],
    ) -> errors::Result<()> {
        let element_class = self.find_class("java/lang/String")?;
        let string_array =
            self.new_object_array(vals.len() as jsize, element_class, JString::default())?;

        for (index, value) in vals.iter().enumerate() {
            let value = self.new_string(value)?;
            self.set_object_array_element(&string_array, index as jsize, value)?;
        }

        let array_obj = JObject::from(string_array);
        self.set_field(obj, name, "[Ljava/lang/String;", JValue::from(&array_obj))
    }
}

pub trait JNI<'local> {
    fn get_path(&mut self, path: &JString) -> String;
    fn read<F>(&mut self, read: F) -> jlong
    where
        F: FnOnce() -> Option<Sac>;
    fn write<F>(&mut self, write: F)
    where
        F: FnOnce() -> Option<()>;
    fn new_floatarray(&mut self, length: jsize) -> JFloatArray<'local>;
    fn set_floatarray(&mut self, array: &JFloatArray, buf: &[jfloat]);
    fn get_floatarray(&mut self, array: &JFloatArray, buf: &mut [jfloat]);

    #[inline]
    fn get_sac_endian(&self, value: jint) -> Endian {
        match value {
            0 => Endian::Little,
            1 => Endian::Big,
            _ => unreachable!(),
        }
    }

    fn new_sac_header(&mut self, sac: &Sac) -> errors::Result<JObject<'local>>;

    fn get_sac_header(&mut self, obj: &JObject) -> errors::Result<SacHeader>;
}

impl<'a> JNI<'a> for JNIEnv<'a> {
    #[inline]
    fn get_path(&mut self, path: &JString) -> String {
        match self.get_string(&path) {
            Ok(path) => path.into(),
            Err(err) => {
                self.throw_new("java/lang/IllegalArgumentException", err.to_string())
                    .unwrap_or_else(|e| {
                        eprintln!("{e}");
                    });

                String::default()
            }
        }
    }

    #[inline]
    fn read<F>(&mut self, read: F) -> jlong
    where
        F: FnOnce() -> Option<Sac>,
    {
        match read() {
            Some(v) => Box::into_raw(Box::new(v)) as jlong,
            None => {
                self.throw_new("java/io/IOException", "")
                    .unwrap_or_else(|e| {
                        eprintln!("{e}");
                    });

                jlong::default()
            }
        }
    }

    #[inline]
    fn write<F>(&mut self, write: F)
    where
        F: FnOnce() -> Option<()>,
    {
        match write() {
            Some(_) => {}
            None => {
                self.throw_new("java/io/IOException", "")
                    .unwrap_or_else(|e| {
                        eprintln!("{e}");
                    });
            }
        }
    }

    #[inline]
    fn new_floatarray(&mut self, length: jsize) -> JFloatArray<'a> {
        match self.new_float_array(length) {
            Ok(array) => array,
            Err(err) => {
                self.throw_new("java/lang/RuntimeException", err.to_string())
                    .unwrap_or_else(|e| {
                        eprintln!("{e}");
                    });

                JFloatArray::default()
            }
        }
    }

    #[inline]
    fn set_floatarray(&mut self, array: &JFloatArray, buf: &[jfloat]) {
        match self.set_float_array_region(array, 0, buf) {
            Ok(_) => {}
            Err(err) => {
                self.throw_new("java/lang/RuntimeException", err.to_string())
                    .unwrap_or_else(|e| {
                        eprintln!("{e}");
                    });
            }
        }
    }

    #[inline]
    fn get_floatarray(&mut self, array: &JFloatArray, buf: &mut [jfloat]) {
        match self.get_float_array_region(array, 0, buf) {
            Ok(_) => {}
            Err(err) => {
                self.throw_new("java/lang/RuntimeException", err.to_string())
                    .unwrap_or_else(|e| {
                        eprintln!("{e}");
                    });
            }
        }
    }

    fn new_sac_header(&mut self, sac: &Sac) -> errors::Result<JObject<'a>> {
        let class = self.find_class("dev/sanmer/sac/io/SacHeader")?;
        let obj = self.alloc_object(class)?;

        self.set_float_field(&obj, "delta", sac.delta)?;
        self.set_float_field(&obj, "depmin", sac.depmin)?;
        self.set_float_field(&obj, "depmax", sac.depmax)?;
        self.set_float_field(&obj, "scale", sac.scale)?;
        self.set_float_field(&obj, "odelta", sac.odelta)?;
        self.set_float_field(&obj, "b", sac.b)?;
        self.set_float_field(&obj, "e", sac.e)?;
        self.set_float_field(&obj, "o", sac.o)?;
        self.set_float_field(&obj, "a", sac.a)?;
        self.set_float_array_field(&obj, "t", &sac.t)?;
        self.set_float_field(&obj, "f", sac.f)?;
        self.set_float_array_field(&obj, "resp", &sac.resp)?;
        self.set_float_field(&obj, "stla", sac.stla)?;
        self.set_float_field(&obj, "stlo", sac.stlo)?;
        self.set_float_field(&obj, "stel", sac.stel)?;
        self.set_float_field(&obj, "stdp", sac.stdp)?;
        self.set_float_field(&obj, "evla", sac.evla)?;
        self.set_float_field(&obj, "evlo", sac.evlo)?;
        self.set_float_field(&obj, "evel", sac.evel)?;
        self.set_float_field(&obj, "evdp", sac.evdp)?;
        self.set_float_field(&obj, "mag", sac.mag)?;
        self.set_float_array_field(&obj, "user", &sac.user)?;
        self.set_float_field(&obj, "dist", sac.dist)?;
        self.set_float_field(&obj, "az", sac.az)?;
        self.set_float_field(&obj, "baz", sac.baz)?;
        self.set_float_field(&obj, "gcarc", sac.gcarc)?;
        self.set_float_field(&obj, "depmen", sac.depmen)?;
        self.set_float_field(&obj, "cmpaz", sac.cmpaz)?;
        self.set_float_field(&obj, "cmpinc", sac.cmpinc)?;
        self.set_float_field(&obj, "xminimum", sac.xminimum)?;
        self.set_float_field(&obj, "xmaximum", sac.xmaximum)?;
        self.set_float_field(&obj, "yminimum", sac.yminimum)?;
        self.set_float_field(&obj, "ymaximum", sac.ymaximum)?;
        self.set_int_field(&obj, "nzyear", sac.nzyear)?;
        self.set_int_field(&obj, "nzjday", sac.nzjday)?;
        self.set_int_field(&obj, "nzhour", sac.nzhour)?;
        self.set_int_field(&obj, "nzmin", sac.nzmin)?;
        self.set_int_field(&obj, "nzsec", sac.nzsec)?;
        self.set_int_field(&obj, "nzmsec", sac.nzmsec)?;
        self.set_int_field(&obj, "nvhdr", sac.nvhdr)?;
        self.set_int_field(&obj, "norid", sac.norid)?;
        self.set_int_field(&obj, "nevid", sac.nevid)?;
        self.set_int_field(&obj, "npts", sac.npts)?;
        self.set_int_field(&obj, "nwfid", sac.nwfid)?;
        self.set_int_field(&obj, "nxsize", sac.nxsize)?;
        self.set_int_field(&obj, "nysize", sac.nysize)?;
        self.set_int_field(&obj, "iftype", sac.iftype.into())?;
        self.set_int_field(&obj, "idep", sac.idep)?;
        self.set_int_field(&obj, "iztype", sac.iztype)?;
        self.set_int_field(&obj, "iinst", sac.iinst)?;
        self.set_int_field(&obj, "istreg", sac.istreg)?;
        self.set_int_field(&obj, "ievreg", sac.ievreg)?;
        self.set_int_field(&obj, "ievtyp", sac.ievtyp)?;
        self.set_int_field(&obj, "iqual", sac.iqual)?;
        self.set_int_field(&obj, "isynth", sac.isynth)?;
        self.set_int_field(&obj, "imagtyp", sac.imagtyp)?;
        self.set_int_field(&obj, "imagsrc", sac.imagsrc)?;
        self.set_boolean_field(&obj, "leven", sac.leven)?;
        self.set_boolean_field(&obj, "lpspol", sac.lpspol)?;
        self.set_boolean_field(&obj, "lovrok", sac.lovrok)?;
        self.set_boolean_field(&obj, "lcalda", sac.lcalda)?;
        self.set_string_field(&obj, "kstnm", &sac.kstnm)?;
        self.set_string_field(&obj, "kevnm", &sac.kevnm)?;
        self.set_string_field(&obj, "khole", &sac.khole)?;
        self.set_string_field(&obj, "ko", &sac.ko)?;
        self.set_string_field(&obj, "ka", &sac.ka)?;
        self.set_string_array_field(&obj, "kt", &sac.kt)?;
        self.set_string_field(&obj, "kf", &sac.kf)?;
        self.set_string_field(&obj, "kuser0", &sac.kuser0)?;
        self.set_string_field(&obj, "kuser1", &sac.kuser1)?;
        self.set_string_field(&obj, "kuser2", &sac.kuser2)?;
        self.set_string_field(&obj, "kcmpnm", &sac.kcmpnm)?;
        self.set_string_field(&obj, "knetwk", &sac.knetwk)?;
        self.set_string_field(&obj, "kdatrd", &sac.kdatrd)?;
        self.set_string_field(&obj, "kinst", &sac.kinst)?;

        Ok(obj)
    }

    fn get_sac_header(&mut self, obj: &JObject) -> errors::Result<SacHeader> {
        let h = SacHeader {
            delta: self.get_float_field(obj, "delta")?,
            depmin: self.get_float_field(obj, "depmin")?,
            depmax: self.get_float_field(obj, "depmax")?,
            scale: self.get_float_field(obj, "scale")?,
            odelta: self.get_float_field(obj, "odelta")?,
            b: self.get_float_field(obj, "b")?,
            e: self.get_float_field(obj, "e")?,
            o: self.get_float_field(obj, "o")?,
            a: self.get_float_field(obj, "a")?,
            t: self.get_float_array_field(obj, "t")?,
            f: self.get_float_field(obj, "f")?,
            resp: self.get_float_array_field(obj, "resp")?,
            stla: self.get_float_field(obj, "stla")?,
            stlo: self.get_float_field(obj, "stlo")?,
            stel: self.get_float_field(obj, "stel")?,
            stdp: self.get_float_field(obj, "stdp")?,
            evla: self.get_float_field(obj, "evla")?,
            evlo: self.get_float_field(obj, "evlo")?,
            evel: self.get_float_field(obj, "evel")?,
            evdp: self.get_float_field(obj, "evdp")?,
            mag: self.get_float_field(obj, "mag")?,
            user: self.get_float_array_field(obj, "user")?,
            dist: self.get_float_field(obj, "dist")?,
            az: self.get_float_field(obj, "az")?,
            baz: self.get_float_field(obj, "baz")?,
            gcarc: self.get_float_field(obj, "gcarc")?,
            depmen: self.get_float_field(obj, "depmen")?,
            cmpaz: self.get_float_field(obj, "cmpaz")?,
            cmpinc: self.get_float_field(obj, "cmpinc")?,
            xminimum: self.get_float_field(obj, "xminimum")?,
            xmaximum: self.get_float_field(obj, "xmaximum")?,
            yminimum: self.get_float_field(obj, "yminimum")?,
            ymaximum: self.get_float_field(obj, "ymaximum")?,
            nzyear: self.get_int_field(obj, "nzyear")?,
            nzjday: self.get_int_field(obj, "nzjday")?,
            nzhour: self.get_int_field(obj, "nzhour")?,
            nzmin: self.get_int_field(obj, "nzmin")?,
            nzsec: self.get_int_field(obj, "nzsec")?,
            nzmsec: self.get_int_field(obj, "nzmsec")?,
            nvhdr: self.get_int_field(obj, "nvhdr")?,
            norid: self.get_int_field(obj, "norid")?,
            nevid: self.get_int_field(obj, "nevid")?,
            npts: self.get_int_field(obj, "npts")?,
            nwfid: self.get_int_field(obj, "nwfid")?,
            nxsize: self.get_int_field(obj, "nxsize")?,
            nysize: self.get_int_field(obj, "nysize")?,
            iftype: self.get_int_field(obj, "iftype")?.into(),
            idep: self.get_int_field(obj, "idep")?,
            iztype: self.get_int_field(obj, "iztype")?,
            iinst: self.get_int_field(obj, "iinst")?,
            istreg: self.get_int_field(obj, "istreg")?,
            ievreg: self.get_int_field(obj, "ievreg")?,
            ievtyp: self.get_int_field(obj, "ievtyp")?,
            iqual: self.get_int_field(obj, "iqual")?,
            isynth: self.get_int_field(obj, "isynth")?,
            imagtyp: self.get_int_field(obj, "imagtyp")?,
            imagsrc: self.get_int_field(obj, "imagsrc")?,
            leven: self.get_boolean_field(obj, "leven")?,
            lpspol: self.get_boolean_field(obj, "lpspol")?,
            lovrok: self.get_boolean_field(obj, "lovrok")?,
            lcalda: self.get_boolean_field(obj, "lcalda")?,
            kstnm: self.get_string_field(obj, "kstnm")?,
            kevnm: self.get_string_field(obj, "kevnm")?,
            khole: self.get_string_field(obj, "khole")?,
            ko: self.get_string_field(obj, "ko")?,
            ka: self.get_string_field(obj, "ka")?,
            kt: self.get_string_array_field(obj, "kt")?,
            kf: self.get_string_field(obj, "kf")?,
            kuser0: self.get_string_field(obj, "kuser0")?,
            kuser1: self.get_string_field(obj, "kuser1")?,
            kuser2: self.get_string_field(obj, "kuser2")?,
            kcmpnm: self.get_string_field(obj, "kcmpnm")?,
            knetwk: self.get_string_field(obj, "knetwk")?,
            kdatrd: self.get_string_field(obj, "kdatrd")?,
            kinst: self.get_string_field(obj, "kinst")?,
        };

        Ok(h)
    }
}

#[no_mangle]
pub extern "system" fn Java_dev_sanmer_sac_io_Sac_read(
    mut env: JNIEnv,
    _class: JClass,
    path: JString,
    endian: jint,
) -> jlong {
    let path = env.get_path(&path);
    let path = Path::new(&path);
    let endian = env.get_sac_endian(endian);

    env.read(|| Sac::from_file(path, endian))
}

#[no_mangle]
pub extern "system" fn Java_dev_sanmer_sac_io_Sac_empty(
    _env: JNIEnv,
    _class: JClass,
) -> jlong {
    let sac = Sac::empty();
    Box::into_raw(Box::new(sac)) as jlong
}

#[no_mangle]
pub unsafe extern "system" fn Java_dev_sanmer_sac_io_Sac_write(
    mut env: JNIEnv,
    _class: JClass,
    ptr: jlong,
    path: JString,
    endian: jint,
) {
    let path = env.get_path(&path);
    let path = Path::new(&path);
    let endian = env.get_sac_endian(endian);
    let sac = &*(ptr as *mut Sac);

    env.write(|| sac.to_file(path, endian));
}

#[no_mangle]
pub unsafe extern "system" fn Java_dev_sanmer_sac_io_Sac_getHeader<'local>(
    mut env: JNIEnv<'local>,
    _class: JClass,
    ptr: jlong,
) -> JObject<'local> {
    let sac = &*(ptr as *mut Sac);
    match env.new_sac_header(sac) {
        Ok(obj) => obj,
        Err(err) => {
            env.throw_new("java/lang/IllegalArgumentException", err.to_string())
                .unwrap_or_else(|e| {
                    eprintln!("{e}");
                });

            JObject::null()
        }
    }
}

#[no_mangle]
pub unsafe extern "system" fn Java_dev_sanmer_sac_io_Sac_setHeader(
    mut env: JNIEnv,
    _class: JClass,
    ptr: jlong,
    header: JObject,
) {
    let sac = &mut *(ptr as *mut Sac);
    match env.get_sac_header(&header) {
        Ok(h) => sac.set_header(h),
        Err(err) => {
            env.throw_new("java/lang/IllegalArgumentException", err.to_string())
                .unwrap_or_else(|e| {
                    eprintln!("{e}");
                });
        }
    }
}

#[no_mangle]
pub unsafe extern "system" fn Java_dev_sanmer_sac_io_Sac_getFirst<'local>(
    mut env: JNIEnv<'local>,
    _obj: JObject,
    ptr: jlong,
) -> JFloatArray<'local> {
    let sac = &*(ptr as *mut Sac);

    let array = env.new_floatarray(sac.first.len() as jsize);
    env.set_floatarray(&array, &sac.first);

    array
}

#[no_mangle]
pub unsafe extern "system" fn Java_dev_sanmer_sac_io_Sac_setFirst(
    mut env: JNIEnv,
    _obj: JObject,
    ptr: jlong,
    array: JFloatArray,
) {
    let sac = &mut *(ptr as *mut Sac);
    env.get_floatarray(&array, &mut sac.first);
}

#[no_mangle]
pub unsafe extern "system" fn Java_dev_sanmer_sac_io_Sac_getSecond<'local>(
    mut env: JNIEnv<'local>,
    _obj: JObject,
    ptr: jlong,
) -> JFloatArray<'local> {
    let sac = &*(ptr as *mut Sac);

    let array = env.new_floatarray(sac.second.len() as jsize);
    env.set_floatarray(&array, &sac.second);

    array
}

#[no_mangle]
pub unsafe extern "system" fn Java_dev_sanmer_sac_io_Sac_setSecond(
    mut env: JNIEnv,
    _obj: JObject,
    ptr: jlong,
    array: JFloatArray,
) {
    let sac = &mut *(ptr as *mut Sac);
    env.get_floatarray(&array, &mut sac.second);
}

#[no_mangle]
pub unsafe extern "system" fn Java_dev_sanmer_sac_io_Sac_drop(
    _env: JNIEnv,
    _class: JClass,
    ptr: jlong,
) {
    let sac = Box::from_raw(ptr as *mut Sac);
    drop(sac);
}
