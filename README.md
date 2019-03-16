Extended Shaders mod for Minecraft 1.12.2.  
The purpose of this API, and it's containing coremod, Extended Shaders, is to allow mods to use shaders and post-processors inside Minecraft without needing to learn how to make a coremod, and without sacrificing compatibility with other mods using this shader system.  
It is designed to allow multiple mods to "attach" shader uniforms, variables, constants, and code without conflicting with each other, in a way not provided by GLSL itself.  
It is NOT intended to be used by someone without a good understanding of GLSL.  
  
HOW TO USE:  
  
  You will need to add the API classes to your project, PRESERVING the package names.  
  HOW TO MAKE A SHADER:  
    TO MAKE A VERTEX SHADER:  
      1. create a file in your mod's assets, containing a list of the uniforms, variables, and constants, like this:  
        uniform float burnAmount; //uniform  
        varying vec4 fragCol; //variable  
        #define col vec3(.976, .969, .816) //constant  
      2. create a file in your mod's assets, containing the code for the shader, like this:  
        fragCol = fragCol * col;  
      3. create a new ShaderData instance, like this:  
        vertShader = new ShaderData(new ResourceLocation(modid, uniformslocation), new ResourceLocation(modid, codelocation), uniform name 1, uniform name 2, ...);  
      4. to turn it ON, do this:  
        ShaderRegistry.addVertexShader(vertShader);  
      5. to turn it OFF, do this:  
        ShaderRegistry.removeVertexShader(vertShader);  
    TO MAKE A FRAGMENT SHADER:  
      1. create a file in your mod's assets, containing a list of the uniforms, variables, and constants, like this:  
        uniform float burnAmount; //uniform  
        varying vec2 textureCoords; //variable  
        #define col vec3(.976, .969, .816) //constant  
      2. create a file in your mod's assets, containing the code for the shader. you will work via modifying gl_fragData[0], or setting it if you prefer, like this:  
        float depth = sqrt(eyePos.x * eyePos.x + eyePos.y * eyePos.y + eyePos.z * eyePos.z);  
        if (depth > 64.0) discard;  
        depth = clamp(depth / 64.0, 0, 1);  
        float val = (max(max(gl_FragData[0].r, gl_FragData[0].g), gl_FragData[0].b) + min(min(gl_FragData[0].r, gl_FragData[0].g), gl_FragData[0].b)) * .25;  
        gl_FragData[0] = vec4(vec3(1.0) - col * (depth + val - depth * val), gl_FragData[0].a);  
      3. create a new ShaderData instance, like this:  
        fragShader = new ShaderData(new ResourceLocation(modid, uniformslocation), new ResourceLocation(modid, codelocation), uniform name 1, uniform name 2, ...);  
      4. to turn it ON, do this:  
        ShaderRegistry.addFragmentShader(fragShader);  
      5. to turn it OFF, do this:  
        ShaderRegistry.removeFragmentShader(fragShader);  
    PROVIDED UNIFORMS/VARIABLES:  
      check src\main\resources\assets\extendedshaders\shaders\shader_uniform.txt  
  HOW TO MAKE A POST-PROCESSOR:  
    1. create a file in your mod's assets, containing a list of the post-processor's uniforms and constants, like this:  
      uniform float currentTime; //uniform  
      #define rad 3.0 //constant  
    2. create a file in your mod's assets, conatining the post-processor's code, like this:  
      float tx1 = rad * dx;  
      float tx2 = tx1 * .5;  
      float ty = rad * .5 * sqrt(3.0) * dy;  
      vec3 cp1 = texture2D(tex0, texCoords).rgb;  
      vec3 cp1 = texture2D(tex0, texCoords + vec2(tx1, 0)).rgb;  
      vec3 cp3 = texture2D(tex0, texCoords + vec2(tx2, ty)).rgb;  
      vec3 cp4 = texture2D(tex0, texCoords + vec2(-tx2, ty)).rgb;  
      vec3 cp5 = texture2D(tex0, texCoords + vec2(-tx1, 0)).rgb;  
      vec3 cp6 = texture2D(tex0, texCoords + vec2(-tx2, -ty)).rgb;  
      vec3 cp7 = texture2D(tex0, texCoords + vec2(tx2, -ty)).rgb;  
      gl_FragData[0] = vec4((cp1 + cp2 + cp3 + cp4 + cp5 + cp6 + cp7) / 7, 1);  
    3. create a new PostProcessor instance, like this  
      postProcessor = new PostProcessor(new ResourceLocation(modid, uniformslocation), new ResourceLocation(modid, codelocation), uniform name 1, uniform name 2, ...);  
    4. to turn it ON use this:  
      PostProcessorRegistry.addPostProcessor(postProcessor);  
    5. to turn it OFF use this:  
      PostProcessorRegistry.removePostProcessor(postProcessor);  
    PROVIDED UNIFORMS/VARIABLES: check src\main\resources\assets\extendedshaders\shaders\post_processor_frag_uniforms.txt  
  
The API also allows for priorities on shaders and post-processors. Look through the API for more information on these advanced features.  
  